import os


def delete_files(dir_path):
    for root, dirs,files in os.walk(dir_path):
        for file in files:
            os.remove(os.path.join(root,file))



delete_files("E:\\info\\LICENTA2020\\wevb\\webapi\\static\\base_smiling_photos")