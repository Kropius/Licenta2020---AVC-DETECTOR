from webapi.models import User,NormalPhoto,SmilingPhoto
from webapi import app,bcrypt,db
from flask import request,jsonify
from webapi.models import User,NormalPhoto,SmilingPhoto
from sqlalchemy import exc


@app.route('/register',methods = ['GET','POST'])
def register():
    if request.method == "POST":
        username = request.form['username']
        password = request.form['password']
        email = request.form['email']
        image = request.files['normal_photo']
        smiling_image = request.files['smiling_photo']
        print(f"I have received {username} {password} {email}")
        hashed_passwd=bcrypt.generate_password_hash(password)
        user = User(username = username,password=hashed_passwd.decode('utf-8'),email = email)
        try:
            db.session.add(user)
            db.session.commit()
        except exc.IntegrityError as err:
            if "user.email" in str(err):
                error = "Email already exists!"
            elif "user.username" in str(err):
                error = "Username already exists!"
            return jsonify({"success":str(error)})
    return jsonify({"success":"success"})

