import secrets
import os
from webapi.models import User
from webapi import app
# from webapi.detection.preprocessing import PreprocessData
import json


def save_photo(path, image):
    random_hex = secrets.token_hex(8)

    _, extension = os.path.splitext(image)
    filename = random_hex + extension
    picture_path = os.path.join(path, filename)
    return picture_path


def create_json_file(file_name):
    with open(os.path.join("webapi/json_files", file_name + ".json"), "w") as my_file:
        json.dump({"empty": True}, my_file)


def write_data(key, data, file_name):
    with open(os.path.join("webapi/json_files", file_name + ".json"), "r") as my_file:
        old_data = json.load(my_file)
    old_data.update({key: data})
    print(old_data)
    with open(os.path.join("webapi/json_files", file_name + ".json"), "w") as my_file:
        json.dump(old_data, my_file)


# def calculate_user_data(my_user):
#     preprocessor = PreprocessData()
#     print(type(my_user.normal_photo.photo_name))
#     normal_image_data = preprocessor.return_face_parts(my_user.normal_photo.photo_name)
#     smiling_image_data  = preprocessor.return_smile_corners(my_user.smiling_photo.photo_name)
#     total_letters,mistakes = my_user.typing_test.total_letters,my_user.typing_test.mistakes
#     nr_mistakes = preprocessor.check_slurred_speech(my_user.recording.recording_name,my_user.recording.id_text)
#     return normal_image_data,smiling_image_data,total_letters,mistakes,nr_mistakes
