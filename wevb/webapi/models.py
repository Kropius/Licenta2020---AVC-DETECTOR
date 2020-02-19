from webapi import db
from datetime import datetime


class User(db.Model):
    id = db.Column(db.Integer,  primary_key=True)
    username = db.Column(db.String(60), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password = db.Column(db.String(60), nullable=False)
    normal_photo = db.relationship('NormalPhoto', backref='owner', )

    def __repr__(self):
        return f"This is the user {self.username}, email address is {self.email}, his password is {self.password}"


class NormalPhoto(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    photo_name = db.Column(db.Integer, nullable=False, unique=True)
    photo_size = db.Column(db.Integer, nullable=False)
    photo_date= db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False,unique=True)

    def __repr__(self):
        return f" This is the photo {self.photo_name}, photo date is {self.photo_date}"


class SmilingPhoto(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    photo_name = db.Column(db.Integer, nullable=False, unique=True)
    photo_size = db.Column(db.Integer, nullable=False)
    photo_date= db.Column(db.DateTime, nullable=False, default=datetime.utcnow)

    def __repr__(self):
        return f" This is the smiling photo {self.photo_name}, photo date is {self.photo_date}"
