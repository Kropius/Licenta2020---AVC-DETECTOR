from webapi.models import User, NormalPhoto, SmilingPhoto, TypingTest, RecordingTest
from webapi import app, bcrypt, db, conn
from flask import request, jsonify
from sqlalchemy import exc
from webapi.functionalities import save_photo
from webapi.detection.preprocessing import PreprocessData
from flask_jwt_extended import jwt_required, create_access_token, get_jwt_identity, create_refresh_token, \
    jwt_refresh_token_required
import random, os, json


def authenticate(username, password):
    user = User.find_by_username(username)
    if user and bcrypt.check_password_hash(user.password, password):
        return user


def identity(payload):
    username = payload['identity']
    return User.find_by_id(username)


@app.route('/get_auth_token', methods=['POST'])
@jwt_refresh_token_required
def get_new_authorization_token():
    if request.method == 'POST':
        current_user = get_jwt_identity()
        ret = {
            'access_token': create_access_token(identity=current_user)
        }
    return jsonify(ret), 200


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        user = User.query.filter_by(username=request.form['username']).first()
        if user and bcrypt.check_password_hash(user.password, request.form['password']):
            ret = {
                'access_token': create_access_token(identity=user.username),
                'refresh_token': create_refresh_token(identity=user.username)
            }
            return jsonify(ret), 200

        else:
            return jsonify({"success": "Incorrect data!"})


@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == "POST":
        preprocessator = PreprocessData()
        username = request.form['username']
        password = request.form['password']
        email = request.form['email']
        image = request.files['normal_photo']
        smiling_image = request.files['smiling_photo']
        recording = request.files['recording']
        recording_id_text = request.form['recording_id_text']
        typed_text = request.form['typed_text']
        typed_text_id = request.form['typed_text_id']

        data = preprocessator.check_similarity(typed_text_id, typed_text)
        hashed_passwd = bcrypt.generate_password_hash(password)
        user = User(username=username, password=hashed_passwd.decode('utf-8'), email=email)
        user.save_to_db()
        try:
            db.session.add(user)
            db.session.commit()
        except exc.IntegrityError as err:
            if "user.email" in str(err):
                error = "Email already exists!"
            elif "user.username" in str(err):
                error = "Username already exists!"
            return jsonify({"success": str(error)})
        else:
            normal_path = save_photo('static/base_normal_photos', image.filename)
            image.save(normal_path)
            smiling_path = save_photo('static/base_smiling_photos', smiling_image.filename)
            smiling_image.save(smiling_path)
            recording_path = save_photo('static/normal_recordings', recording.filename)
            recording.save(recording_path)
            # type_test = TypingTest(total_letters = )
            NormalPhoto(photo_name=normal_path, photo_size=0, user_id=user.id).save_to_db()
            SmilingPhoto(photo_name=smiling_path, photo_size=0, user_id=user.id).save_to_db()
            RecordingTest(recording_name=recording_path, id_text=recording_id_text,
                          user_id=user.id).save_to_db()
            TypingTest(total_letters=data[1], mistakes=data[0], user_id=user.id).save_to_db()
    return jsonify({"success": "You have successfully registered!"})


@app.route('/check_symmetry_normal_img', methods=['GET', 'POST'])
@jwt_required
def check_symmetry_normal_photo():
    """
    We received a normal photo of the user and we will return an array with the most important data
    :return:
    """
    if request.method == 'POST':
        preprocess = PreprocessData()
        # print(request.files)
        f = request.files['image']
        filename = f.filename
        f.save(os.path.join("webapi/UPLOAD_FOLDER_PHOTOS", filename))
        x = User.query.filter_by(username=get_jwt_identity()).first()
    return jsonify("success")


@app.route('/get_smiley_corners', methods=['GET', 'POST'])
@jwt_required
def get_smiley_corners():
    """
    We received a smiling photo of the user and we will return an array with the most important data
    :return:
    """
    if request.method == 'POST':
        preprocess = PreprocessData()
        f = request.files['image']
        filename = f.filename
        f.save(os.path.join(app.config['UPLOAD_FOLDER_PHOTOS'], filename))

    return jsonify(preprocess.return_smile_corners(os.path.join(app.config['UPLOAD_FOLDER_PHOTOS'], filename)))


@app.route('/get_text', methods=['GET'])
@jwt_required
def get_text():
    """
    Returns a random text from the database
    :return:
    """
    if request.method == "GET":
        texts = conn.cursor().execute("select * from texts").fetchall()
        id, text = random.choice(texts)
    return jsonify({"id": id, "text": text})


@app.route('/parse_voice', methods=['GET', 'POST'])
@jwt_required
def parse_voice():
    """
    We receive a recording and the id of the text that we have sent and send back the differences bettween the recording and the text
    :return:
    """
    if request.method == "POST":
        # recordingul
        preprocess = PreprocessData()
        f = request.files['recording']
        # id-ul textului
        id_text = request.form.getlist('id_text')
        filename = f.filename
        f.save(os.path.join("webapi/UPLOAD_FOLDER_RECORDINGS", filename))
        # aflam ce a zis de fapt vorbitorul
        said = preprocess.get_text_from_wav(
            os.path.join("webapi/UPLOAD_FOLDER_RECORDINGS", filename))
        # vrem sa determinam asemanarea dintre ce a zis si ce trebuia sa zica
        nr_mistakes = preprocess.check_slurred_speech(said, int(id_text[0]))
        user = User.query.filter_by(username=get_jwt_identity()).first()
    return jsonify({"speech_test": nr_mistakes})


@app.route('/send_texting_test', methods=['GET', 'POST'])
@jwt_required
def send_texting_test():
    """
    We received the text_id and the input_text which the user had to text
    :return:
    """
    if request.method == 'POST':
        preprocess = PreprocessData()
        id_text = int(request.form.getlist('id_text')[0])
        input_text = request.form.getlist('input_text')[0]
        # print(id_text)
        differences = preprocess.check_similarity(id_text, input_text)
    return jsonify({"mistakes": differences[0], "total_letters": differences[1]})


@app.route('/send_final_result', methods=['GET', 'POST'])
def send_final_result():
    """
    At this point we have all the necessary data to make a comparision between the database and the info we have received now.
    We will return True or False
    :return:
    """
    if request.method == "POST":
        # todo build the dictionary for the class that takes decidsion
        preprocess = PreprocessData
        data = request.get_data().decode('utf-8')
        data = data.replace("\\n", "\n")
        data = data.replace("&", ", ")
        good_data = "{" + data + "}"
        json_file = json.loads(good_data)
        data = preprocess.build_data_for_decision(json_file)
        # builder = take_decision.builder(data)
        # print(builder)
    # return jsonify({"verditct": 1 if builder.compute_symptoms() else 0})
