import speech_recognition as sr
import Levenshtein as lv
from webapi import conn
from imutils import face_utils
import numpy as np
import argparse
import imutils
import dlib
import cv2


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
