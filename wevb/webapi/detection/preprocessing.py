import speech_recognition as sr
import Levenshtein as lv
from webapi import conn
from imutils import face_utils
import numpy as np
import argparse
import imutils
import dlib
import cv2
import json
from webapi.functionalities import create_json_file,write_data
from webapi.detection.take_decision import builder
import os
from webapi import app


class PreprocessData:

    def get_text_from_wav(self, wav_file):
        r = sr.Recognizer()
        with sr.AudioFile(wav_file) as source:
            # reads the audio file. Here we use record instead of
            # listen
            audio = r.record(source)

        try:
            text = r.recognize_google(audio)
            print(f"Recognized Text: {text}")
            return text
        except:
            print("Sorry could not recognize what you said")

    def check_slurred_speech(self, wav_text, id_text):
        # aflam ceea ce trebuia sa zica folosind
        text = conn.execute("select text from texts where id = (?)", (id_text,)).fetchone()[0]
        return self.compare_two_texts(wav_text, text)

    def compare_two_texts(self, text_said, original_text):
        # todo maybe upgrade
        text_said = text_said.split(' ')
        original_text = original_text.split(' ')
        mistakes = 0
        for i in range(min(len(original_text), len(text_said))):
            if text_said[i] != original_text[i]:
                mistakes += 1
        return mistakes

    def check_similarity(self, original_text_id, input_text):
        text = conn.execute("select text from texts where id = (?)", (original_text_id,)).fetchone()[0]
        return lv.distance(text, input_text), len("".join(text))

    def detecting_face_parts(self, image_path):
        detector = dlib.get_frontal_face_detector()
        predictor = dlib.shape_predictor("webapi/static/detection_files/shape_predictor_68_face_landmarks.dat")

        # load the input image, resize it, and convert it to grayscale
        print(image_path)
        image = cv2.imread(image_path)
        image = imutils.resize(image, width=500)
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        # detect faces in the grayscale image
        rects = detector(gray, 1)
        # loop over the face detections
        output_coords = []
        for (i, rect) in enumerate(rects):

            shape = predictor(gray, rect)
            shape = face_utils.shape_to_np(shape)

            for (name, (i, j)) in face_utils.FACIAL_LANDMARKS_IDXS.items():

                clone = image.copy()
                cv2.putText(clone, name, (10, 30), cv2.FONT_HERSHEY_SIMPLEX,
                            0.7, (0, 0, 255), 2)

                # loop over the subset of facial landmarks, drawing the
                # specific face part
                for (x, y) in shape[i:j]:
                    cv2.circle(clone, (x, y), 1, (0, 0, 255), -1)
                    # print(x, y)
                    # cv2.imshow("Image", clone)
                    # cv2.waitKey(0)

                my_out = list(map(lambda x: [int(x[0]), int(x[1])], shape[i:j]))

                my_out = list(map(lambda x: tuple(x), my_out))
                output_coords.append({name: my_out})
                # extract the ROI of the face region as a separate image
                (x, y, w, h) = cv2.boundingRect(np.array([shape[i:j]]))
                roi = image[y:y + h, x:x + w]
                roi = imutils.resize(roi, width=250, inter=cv2.INTER_CUBIC)

                # show the particular face part
                # cv2.imshow("ROI", roi)
                # cv2.imshow("Image", clone)
                # cv2.waitKey(0)

            # visualize all facial landmarks with a transparent overlay

            cv2.waitKey(0)
            # build_input_from_photo(output_coords)
        print("face parts",output_coords)
        return output_coords

    def return_face_parts(self, image_path):
        """
        :param image_path: All the coordinates from body faces
        :return:
        """
        print("mata",image_path)
        # print(self.detecting_face_parts(image_path))
        return self.build_input_from_photo(self.detecting_face_parts(image_path))

    # def return_smile_corners(self, image_path):
    #     """
    #     :param output_coords: All the coordinates from body faces
    #     :return: returning only the corners of the mouth
    #     """
    #     full_output = self.build_input_from_photo(self.detecting_face_parts(image_path))
    #     return {'left_mouth_corner_smiling': full_output['left_mouth_corner'],
    #             'right_mouth_corner_smiling': full_output['right_mouth_corner'],
    #             "upper_most_point_smiling": full_output["upper_most_point"],
    #             "lower_most_point_smiling": full_output["lower_most_point"]}

    def build_input_from_photo(self, array_of_coordinates):
        array_of_coordinates = {x: y for i in array_of_coordinates for x, y in i.items()}
        output = dict()
        print(array_of_coordinates)
        mounth = self.prepare_mouth(array_of_coordinates['mouth'][:12])
        left_eye = array_of_coordinates['left_eye']
        right_eye = array_of_coordinates['right_eye']

        output['mouth']= (mounth[0],mounth[1])
        output['left_eye'] = left_eye
        output['right_eye'] = right_eye
        output['mouth_corners'] = (array_of_coordinates['mouth'][0],array_of_coordinates['mouth'][6])
        output['vertical_points'] = (array_of_coordinates['mouth'][3],array_of_coordinates['mouth'][9])
        return output

    def prepare_mouth(self, mouth):
        left_side_up = mouth[:4]
        right_side_up = mouth[4:7]
        right_side_down = mouth[7:9]
        left_side_down = mouth[9:12]
        left_side = left_side_up + left_side_down
        right_side = right_side_up + right_side_down
        # mean_left, variance_left = get_mean_variance(left_side)
        # mean_right, variance_right = get_mean_variance(right_side)
        # print(left_side, right_side)
        return left_side, right_side

    def write_mouth_eyes_data_tojson(self, userdb, path_received_image):
        # print(userdb.normal_photo.photo_name)

        print(path_received_image)
        create_json_file(userdb.username+"now")
        now_data = self.return_face_parts(path_received_image)
        calculator = builder()
        assymetry_mouth =calculator.get_mouth_asymmetry(now_data['mouth'])
        assymetry_eyes = calculator.get_eyes_asymmetry(now_data['left_eye'],now_data['right_eye'])
        distance_corners = calculator.calculate_distance_between_corners(now_data['mouth_corners'])
        vertical_distance = calculator.calculcate_vertical_distance(now_data['vertical_points'])
        data = {'mouth':assymetry_mouth,'eyes':assymetry_eyes,'distance_corners':distance_corners,'vertical_distance':vertical_distance}
        write_data("normal_photo",data,userdb.username+"now")

        create_json_file(userdb.username+"db")
        db_data = self.return_face_parts(userdb.normal_photo.photo_name)
        calculator = builder()
        assymetry_mouth = calculator.get_mouth_asymmetry(db_data['mouth'])
        assymetry_eyes = calculator.get_eyes_asymmetry(db_data['left_eye'], db_data['right_eye'])
        distance_corners = calculator.calculate_distance_between_corners(db_data['mouth_corners'])
        vertical_distance = calculator.calculcate_vertical_distance(db_data['vertical_points'])
        data = {'mouth': assymetry_mouth, 'eyes': assymetry_eyes, 'distance_corners': distance_corners,
                'vertical_distance': vertical_distance}
        write_data("normal_photo", data, userdb.username + "db")

    def write_smiling_data_tojson(self,userdb,path_received_image):
        now_data = self.return_face_parts(path_received_image)
        calculator = builder()
        distance_corners = calculator.calculate_distance_between_corners(now_data['mouth_corners'])
        vertical_distance = calculator.calculcate_vertical_distance(now_data['vertical_points'])
        smiling_data = {"distance_corners":distance_corners,"vertical_distance":vertical_distance}
        write_data("smiling_data",smiling_data,userdb.username+"now")

        db_data = self.return_face_parts(userdb.smiling_photo.photo_name)
        distance_corners = calculator.calculate_distance_between_corners(db_data['mouth_corners'])
        vertical_distance = calculator.calculcate_vertical_distance(db_data['vertical_points'])
        smiling_data = {"distance_corners": distance_corners, "vertical_distance": vertical_distance}
        write_data("smiling_data", smiling_data, userdb.username + "now")
        write_data("smiling_data",smiling_data,userdb.username+"db")

    def write_recording_data(self,userdb,recording_path,id_text):
        now_data = self.check_slurred_speech(recording_path,id_text)
        write_data("speech_mistakes",now_data,userdb.username+"now")
        db_data = self.check_slurred_speech(userdb.recording.recording_name,userdb.recording.id_text)
        write_data("speech_mistakes", db_data, userdb.username + "db")

    def write_texting_data(self,userdb,input_text,id_text):
        now_data = self.check_similarity(id_text,input_text)
        texting_data = {"mistakes":now_data[0],"total_letters":now_data[1]}
        write_data("texting_test",texting_data,userdb.username+"now")

        db_data = userdb.typing_test.mistakes,userdb.typing_test.total_letters
        texting_data = {"mistakes":db_data[0],"total_letters":db_data[1]}
        write_data("texting_test",texting_data,userdb.username+"db")

    def build_data_for_decision_from_json(self, user):
        normal_photo, smiling_photo, mistakes, total_letters, speech_test = self.parse_json_data(user)
        return self.build_data_for_decision(normal_photo, smiling_photo, mistakes, total_letters, speech_test)

    def build_data_for_decision(self, normal_photo, smiling_photo, mistakes, total_letters, speech_test):
        parsed_data = dict()

        parsed_data['left_mouth'] = normal_photo['left_mouth']
        parsed_data['right_mouth'] = normal_photo['right_mouth']
        parsed_data['left_eye'] = normal_photo['left_eye']
        parsed_data['right_eye'] = normal_photo['right_eye']
        parsed_data['left_mouth_corner'] = normal_photo['left_mouth_corner']
        parsed_data['right_mouth_corner'] = normal_photo['right_mouth_corner']
        parsed_data['texting_test'] = (mistakes, total_letters)
        parsed_data['speech_test'] = speech_test
        parsed_data['smiley_corners'] = (
            smiling_photo['left_mouth_corner_smiling'],
            smiling_photo['right_mouth_corner_smiling'])
        parsed_data['upper_point'] = normal_photo['upper_most_point']
        parsed_data['lower_point'] = normal_photo['lower_most_point']
        parsed_data['upper_point_smiling'] = smiling_photo["upper_most_point_smiling"]
        parsed_data['lower_point_smiling'] = smiling_photo["lower_most_point_smiling"]
        print(parsed_data)
        return parsed_data

    def parse_json_data(self, user):
        with open("webapi/json_files/" + user + ".json", "r") as my_json:
            json_data = json.load(my_json)
        normal_photo = json_data["normal_photo"]
        smiling_photo = json_data["smiling_photo"]
        mistakes = json_data['mistakes']
        total_letters = json_data['total_letters']
        speech_test = json_data['speech_text']
        return normal_photo, smiling_photo, mistakes, total_letters, speech_test
