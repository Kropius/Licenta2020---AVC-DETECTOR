from webapi.models import User, NormalPhoto, SmilingPhoto, TypingTest, RecordingTest
from webapi import app, bcrypt, db, conn
from flask import request, jsonify
from webapi.models import User, NormalPhoto, SmilingPhoto
from sqlalchemy import exc
from webapi.functionalities import save_photo
from webapi.detection.preprocessing import PreprocessData
import secrets, random


@app.route('/get_text', methods=['GET'])
def get_voices():
    if request.method == "GET":
        texts = conn.cursor().execute("select * from texts").fetchall()
        id, text = random.choice(texts)
    return jsonify({"id": id, "text": text})


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

        data = preprocessator.check_similarity(typed_text_id,typed_text)
        print(f"I have received {username} {password} {email}")
        hashed_passwd = bcrypt.generate_password_hash(password)
        user = User(username=username, password=hashed_passwd.decode('utf-8'), email=email)
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
            normal_photo = NormalPhoto(photo_name=normal_path, photo_size=0, user_id=user.id)
            smiling_photo = SmilingPhoto(photo_name=smiling_path, photo_size=0, user_id=user.id)
            recording_test = RecordingTest(recording_name=recording_path, id_text=recording_id_text,
                                           user_id=user.id)
            typing_test =TypingTest(total_letters = data[1],mistakes = data[0],user_id  = user.id)
            db.session.add(normal_photo)
            db.session.add(smiling_photo)
            db.session.add(recording_test)
            db.session.add(typing_test)

            db.session.commit()

    return jsonify({"success": "success"})
