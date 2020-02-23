from webapi.models import User, NormalPhoto, SmilingPhoto, TypingTest, RecordingTest
from webapi import app, bcrypt, db, conn
from flask import request, jsonify
from webapi.models import User, NormalPhoto, SmilingPhoto
from sqlalchemy import exc
from webapi.functionalities import save_photo
from webapi.detection.preprocessing import PreprocessData
from flask_login import current_user, login_user
from flask_jwt_extended import jwt_required, create_access_token, get_jwt_identity, create_refresh_token, \
    jwt_refresh_token_required
import secrets, random


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
    if request.methods == 'POST':
        current_user = get_jwt_identity()
        ret = {
            'access_token': create_access_token(identity=current_user)
        }
    return jsonify(ret), 200


@app.route('/get_text', methods=['GET'])
@jwt_required
def get_voices():
    if request.method == "GET":
        texts = conn.cursor().execute("select * from texts").fetchall()
        id, text = random.choice(texts)
        x = User.query.filter_by(username=get_jwt_identity()).first()
        print(x)
    return jsonify({"id": id, "text": text})


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
