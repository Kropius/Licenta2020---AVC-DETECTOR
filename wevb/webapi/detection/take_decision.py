import numpy as np
import json


class builder:
    def __init__(self):
        pass
        # print(data)
        # self.mouth = (data['left_mouth'], data['right_mouth'])
        # self.corners = (data['left_mouth_corner'], data['right_mouth_corner'])
        # self.left_eye = data['left_eye']
        # self.right_eye = data['right_eye']
        # self.smiley_corners = data['smiley_corners']
        # self.texting_test = data['texting_test']
        # self.speech_test = data['speech_test']
        # self.distances = (data['upper_point'], data['lower_point'])
        # self.distances_smiling = (data['upper_point_smiling'], data['lower_point_smiling'])
        # # print(self.calculate_distance_bettween_corners(), self.calculate_distance_bettween_smiling_corners())

    def calculate_asymmetry_mouth(self, mouth):
        left_mean = np.mean(list(map(lambda x: x[1], mouth[0])))
        right_mean = np.mean(list(map(lambda x: x[1], mouth[1])))
        return left_mean, right_mean

    def get_mouth_asymmetry(self, mouth):
        """
        :return: difference between mean of left mouth and right mouth
        """
        left_mean, right_mean = self.calculate_asymmetry_mouth(mouth)
        return abs(left_mean - right_mean)

    def calculate_asymmetry_eyes(self, left_eye, right_eye):
        left_mean = np.mean(list(map(lambda x: x[1], left_eye)))
        right_mean = np.mean(list(map(lambda x: x[1], right_eye)))
        return left_mean, right_mean

    def get_eyes_asymmetry(self, left_eye, right_eye):
        """
        :return: difference bettween mean of left eye and right eye
        """
        left_mean, right_mean = self.calculate_asymmetry_eyes(left_eye, right_eye)
        return abs(left_mean - right_mean)

    def calculate_distance_between_corners(self, corners):
        """
        Calculates the distance between the 2 corners of the mouth.
        :param corners: Corners[0] = left_corner, Corners[1] = right_corner
        :return:
        """
        return np.linalg.norm(np.array(corners[0]) - np.array(corners[1]))

    def calculcate_vertical_distance(self, distances):
        """
        Calculates the vertical distance between the upper most point of the lips and the lower most point
        :param distances:
        :return:
        """
        return np.linalg.norm(np.array(distances[0]) - np.array(distances[1]))

    def detect_moutheyes_abnormalities(self, username):
        """
        We will calculate the differences bettween normal face assymetry and just received face assymetry(eyes and mouth)
        :param username:the username of the user
        :return:
        """
        with open(f"webapi/json_files/{username}db.json")as my_json:
            db_json = json.load(my_json)
        with open(f"webapi/json_files/{username}now.json")as my_json:
            now_json = json.load(my_json)
        mouth_difference = abs(db_json['normal_photo']['mouth'] - now_json['normal_photo']['mouth'])
        eyes_difference = abs(db_json['normal_photo']['eyes'] - now_json['normal_photo']['eyes'])
        return mouth_difference + eyes_difference

    def detect_smiling_abnormalities(self, username):
        with open(f"webapi/json_files/{username}db.json")as my_json:
            db_json = json.load(my_json)
        with open(f"webapi/json_files/{username}now.json")as my_json:
            now_json = json.load(my_json)
        corner_difference = abs(
            db_json['smiling_data']['distance_corners'] - now_json['smiling_data']['distance_corners'])
        vertical_difference = abs(
            db_json['smiling_data']['vertical_distance'] - now_json['smiling_data']['vertical_distance'])
        return corner_difference + vertical_difference

    def detect_speech_abnormalities(self, username):
        with open(f"webapi/json_files/{username}db.json")as my_json:
            db_json = json.load(my_json)
        with open(f"webapi/json_files/{username}now.json")as my_json:
            now_json = json.load(my_json)
        total_mistakes = abs(db_json['speech_mistakes'] - now_json['speech_mistakes'])
        return total_mistakes

    def detect_typing_abnormalities(self, username):
        with open(f"webapi/json_files/{username}db.json")as my_json:
            db_json = json.load(my_json)
        with open(f"webapi/json_files/{username}now.json")as my_json:
            now_json = json.load(my_json)
        total_mistakes = abs((db_json['texting_test']["mistakes"] / db_json['texting_test']["total_letters"]) - (
                now_json['texting_test']["mistakes"] / now_json['texting_test']["total_letters"]))
        return total_mistakes

    def caculate_total_score(self, username):
        score = self.detect_typing_abnormalities(username) + self.detect_speech_abnormalities(
            username) + self.detect_smiling_abnormalities(username) +\
        self.detect_moutheyes_abnormalities(username)
        return score

