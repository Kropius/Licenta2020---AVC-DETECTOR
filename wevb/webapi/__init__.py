from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_jwt_extended import JWTManager
import datetime

import sqlite3

app = Flask(__name__)

my_db = 'webapi\\app.db'
conn = sqlite3.connect(my_db, check_same_thread=False)
app.config.from_pyfile("E:\info\LICENTA2020\wevb\webapi\config.py")
app.permanent_session_lifetime = datetime.timedelta(days=365)
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
jwt = JWTManager(app)


from webapi import routes

