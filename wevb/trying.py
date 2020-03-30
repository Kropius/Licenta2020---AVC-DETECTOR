from pydub import AudioSegment

AudioSegment.converter = "C://Users//Mihnea//Downloads//ffmpeg-20200328-3362330-win64-static//bin//ffmpeg.exe"
sound = AudioSegment.from_file("E:/info/LICENTA2020/wevb/webapi/UPLOAD_FOLDER_RECORDINGS/audiorecordtest.mp3")