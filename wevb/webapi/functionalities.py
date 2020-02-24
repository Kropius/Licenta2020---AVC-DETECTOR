import secrets
import os
# from webapi.models import User
from webapi import app


def save_photo(path, image):
    random_hex = secrets.token_hex(8)

    _, extension = os.path.splitext(image)
    filename = random_hex + extension
    picture_path = os.path.join(app.root_path, path, filename)
    return picture_path

