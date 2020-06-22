from webapi import db
from datetime import datetime


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(60), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password = db.Column(db.String(60), nullable=False)
    normal_photo = db.relationship('NormalPhoto',uselist=False, backref='owner', )
    smiling_photo = db.relationship('SmilingPhoto', uselist=False,backref='owner', )
    typing_test = db.relationship('TypingTest',uselist=False, backref='owner', )
    recording = db.relationship('RecordingTest',uselist=False, backref='owner', )

    def __repr__(self):
        return f"This is the user {self.username}, email address is {self.email}, his password is {self.password}"

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()


class NormalPhoto(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    photo_name = db.Column(db.Integer, nullable=False, unique=True)
    photo_size = db.Column(db.Integer, nullable=False)
    photo_date = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False, unique=True)

    def __repr__(self):
        return f" This is the photo {self.photo_name}, photo date is {self.photo_date} and the user is {self.owner}"

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()


class SmilingPhoto(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    photo_name = db.Column(db.Integer, nullable=False, unique=True)
    photo_size = db.Column(db.Integer, nullable=False)
    photo_date = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"), unique=True)

    def __repr__(self):
        return f" This is the smiling photo {self.photo_name}, photo date is {self.photo_date}"

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()


class TypingTest(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    total_letters = db.Column(db.Integer, nullable=False)
    mistakes = db.Column(db.Integer, nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"), unique=True)

    def __repr__(self):
        return f"This the typing test from {self.owner}. It has {self.mistakes} out of {self.total_letters}"

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()


class RecordingTest(db.Model):
    id = db.Column  (db.Integer, primary_key=True)
    recording_text = db.Column(db.String(220), nullable=False)
    id_text = db.Column(db.Integer, nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"), unique=True)

    def __repr__(self):
        return f"This is the Recording Test from {self.owner}. Recording name is {self.recording_name} and the id is {self.id_text}"

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()
