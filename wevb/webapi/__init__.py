from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
import sqlite3

app = Flask(__name__)

my_db = 'webapi\\app.db'
conn = sqlite3.connect(my_db, check_same_thread=False)
app.config['SQLALCHEMY_DATABASE_URI'] = "sqlite:///site.db"
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
from webapi import routes
