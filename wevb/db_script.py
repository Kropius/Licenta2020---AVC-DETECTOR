import sqlite3

conn = sqlite3.connect('app.db')

try:
    conn.execute('''CREATE TABLE texts
         (ID INT PRIMARY KEY     NOT NULL,
          text varchar(100));''')
except:
    print('Error')


def read_file(file_path):
    with open(file_path, "r") as my_file:
        lines = my_file.readlines()
    lines = list(map(lambda x: x[:-2], lines))
    return lines


def insert(lines):
    for i,line in enumerate(lines):
        print(line)
        conn.execute("insert into texts(id,text) values (?,?);", (i,str(line),))


lines = read_file("texts.txt")
# insert(lines)
# conn.execute('drop table texts;')
print(conn.execute("select * from texts").fetchall())
conn.commit()
