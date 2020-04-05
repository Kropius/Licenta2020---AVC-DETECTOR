from webapi.models import User, NormalPhoto, SmilingPhoto, TypingTest, RecordingTest
from webapi import app, bcrypt, db, conn
from flask import request, jsonify
from sqlalchemy import exc
# from webapi.functionalities import save_photo
from webapi.detection.preprocessing import PreprocessData
from webapi.detection.take_decision import builder
# from webapi.functionalities import calculate_user_data
from flask_jwt_extended import jwt_required, create_access_token, get_jwt_identity, create_refresh_token, \
    jwt_refresh_token_required, JWTManager, get_raw_jwt_header, get_raw_jwt
from webapi.functionalities import save_photo
import random, os, json


def authenticate(username, password):
    user = User.find_by_username(username)
    if user and bcrypt.check_password_hash(user.password, password):
        return user


def identity(payload):
    username = payload['identity']
    print(username)
    print(User.find_by_id(username))
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
            x = create_access_token(identity=user.username)
            print(x)
            ret = {

                'access_token': x,
                'refresh_token': create_refresh_token(identity=user.username),
                "succes": "Correct data!"
            }
            return jsonify(ret), 200

        else:
            return jsonify({"succes": "Incorrect data!"})


@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == "POST":
        print(request.files)
        preprocessator = PreprocessData()
        username = request.form['username']
        password = request.form['password']
        email = request.form['email']
        image = request.files['normal_photo']
        smiling_image = request.files['smiling_photo']
        recording = request.form['recording_text']
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
            normal_path = save_photo('webapi/static/base_normal_photos', image.filename)
            image.save(normal_path)
            smiling_path = save_photo('webapi/static/base_smiling_photos', smiling_image.filename)
            smiling_image.save(smiling_path)

            # type_test = TypingTest(total_letters = )
            NormalPhoto(photo_name=normal_path, photo_size=0, user_id=user.id).save_to_db()
            SmilingPhoto(photo_name=smiling_path, photo_size=0, user_id=user.id).save_to_db()
            RecordingTest(recording_text=recording, id_text=recording_id_text,
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
        print(request.headers)
        print(get_jwt_identity())
        # print(get_raw_jwt_header())
        print(get_raw_jwt())
        preprocess = PreprocessData()
        calculator = builder()
        f = request.files['image']
        filename = f.filename
        f.save(os.path.join(app.config['UPLOAD_FOLDER_PHOTOS'], filename))
        x = User.query.filter_by(username=get_jwt_identity()).first()

        preprocess.write_mouth_eyes_data_tojson(x, os.path.join(app.config['UPLOAD_FOLDER_PHOTOS'], filename))
        score = calculator.detect_moutheyes_abnormalities(x.username)
        if score > 5:
            return jsonify({"response":"Call 911"})
        else:
            return jsonify({"response":"Face ok!"})
    return "Request unauthorized"


@app.route('/get_smiley_corners', methods=['GET', 'POST'])
@jwt_required
def get_smiley_corners():
    """
    We received a smiling photo of the user and we will return an array with the most important data
    :return:
    """

    if request.method == 'POST':
        print(request.files)
        preprocess = PreprocessData()
        calculator = builder()
        f = request.files['image']
        filename = f.filename
        f.save(os.path.join("webapi/UPLOAD_FOLDER_PHOTOS", filename))
        x = User.query.filter_by(username=get_jwt_identity()).first()
        preprocess.write_smiling_data_tojson(x, os.path.join("webapi/UPLOAD_FOLDER_PHOTOS", filename))
        score = calculator.detect_smiling_abnormalities(x.username)
        if score > 15:
            return jsonify({"response":"Call 911!"})
        else:
            return jsonify({"response":"Smile ok!"})
    return "Request unauthorized"


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


@app.route("/parsevoice", methods= ['GET','POST'])
@jwt_required
def parseVoice():
    if request.method == "POST":
        preprocess = PreprocessData()
        calculator = builder()
        id_text = int(request.form.getlist('recording_id_text')[0])
        text = (request.form.getlist('text')[0])
        user = User.query.filter_by(username=get_jwt_identity()).first()

        preprocess.write_recording_data(user,text,id_text)
        score = calculator.detect_speech_abnormalities(user.username)
        if score>5:
            return jsonify({'response':'Call 911'})
        else:
            return jsonify({'response':'Speech ok!'})
    return 'Unauthorized'

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
        calculator = builder()
        f = request.files['recording']
        # id-ul textului
        id_text = int(request.form.getlist('id_text')[0])
        filename = f.filename
        f.save(os.path.join("webapi/UPLOAD_FOLDER_RECORDINGS", filename))
        # print(filename)
        # aflam ce a zis de fapt vorbitorul
        said = preprocess.get_text_from_wav(
            os.path.join("webapi/UPLOAD_FOLDER_RECORDINGS", filename))
        # vrem sa determinam asemanarea dintre ce a zis si ce trebuia sa zica
        # nr_mistakes = preprocess.check_slurred_speech(said, int(id_text[0]))
        user = User.query.filter_by(username=get_jwt_identity()).first()
        preprocess.write_recording_data(user, os.path.join("webapi/UPLOAD_FOLDER_RECORDINGS", filename), id_text)
        score = calculator.detect_speech_abnormalities(user.username)
        if score > 5:
            return jsonify({"response":"Call 911"})
        else:
            return jsonify({"response":"Speech ok!"})
    return "Request unauthorized"


@app.route('/send_texting_test', methods=['POST'])
@jwt_required
def send_texting_test():
    """
    We received the text_id and the input_text which the user had to text
    :return:
    """
    if request.method == 'POST':
        preprocess = PreprocessData()
        calculator = builder()
        id_text = int(request.form.getlist('id_text')[0])
        input_text = request.form.getlist('input_text')[0]
        # print(id_text)
        differences = preprocess.check_similarity(id_text, input_text)
        user = User.query.filter_by(username=get_jwt_identity()).first()
        preprocess.write_texting_data(user, input_text, id_text)
        score = calculator.detect_typing_abnormalities(user.username)
        if score>10:
            return jsonify({"response":"Call 911"})
        return jsonify({"response":"Arms ok!"})
    return "Request unauthorized"


@app.route('/send_final_result', methods=['GET', 'POST'])
@jwt_required
def send_final_result():
    """
    At this point we have all the necessary data to make a comparision between the database and the info we have received now.
    We will return True or False
    :return:
    """
    if request.method == "POST":
        # todo build the dictionary for the class that takes decidsion
        calculator = builder()
        user = User.query.filter_by(username=get_jwt_identity()).first()
        score = calculator.caculate_total_score(user.username)
        if score > 30:
            return "Call 911"
        else:
            return "You are ok! Calm down"
    return "Unauthorized Request"
