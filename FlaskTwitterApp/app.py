from flask import *
import pyrebase
from pathlib import Path
import pandas as pd
import os
import csv


config={
    "apiKey": "AIzaSyBuKINge-3AO9qdnp6jQznVDOYVt4vjCaI",
    "authDomain": "twitterdemodb.firebaseapp.com",
    "databaseURL": "https://twitterdemodb-default-rtdb.firebaseio.com",
    "projectId": "twitterdemodb",
    "storageBucket": "twitterdemodb.appspot.com",
    "messagingSenderId": "482435111987",
    "appId": "1:482435111987:web:c63d4133676dbe93f21746",
    "measurementId": "G-MZ82P2S116"
}
firebase=pyrebase.initialize_app(config)
db=firebase.database()

app = Flask(__name__)
app.secret_key = b'_5#y2L"F4Q8z\n\xec]/'

baslik=("No","Anahtar Kelime","İşlemler")

@app.route('/',methods=['GET','POST'])
def index():

    try:
        kelime = db.child("searchKeys").get()
        getir = kelime.val()
        if request.method == 'POST':
            csv_return(str(request.form['anahtar']))
        return render_template('index.html',headings=baslik,k=getir.keys())

    except BaseException as e:
        print(f'Hata: {str(e)}')



def csv_return(index):

    try:
        tumveriler=db.child("searchKeys").child(index).get()
        #cikti=tumveriler.val()
        p=r'C:/Users/LENOVO/Desktop/'
        path=Path(p+index+'.csv')
        if os.path.exists(path) and path:
            flash(f"{index} dosyası zaten var","danger")
        else:
            with open(path, 'w+', newline='', encoding='utf-8') as csv_file:
                csv_writer = csv.writer(csv_file, delimiter=',')
                baslik = []
                baslik.append('id')
                baslik.append('tweets')
                csv_writer.writerow(baslik)
                yeni_satir = []
                for satir in tumveriler.each():
                    yeni_satir.clear()
                    #yeni_satir.append('{}'.format(satir.val()))
                    yeni_satir.append('{}'.format(satir.key()))
                    yeni_satir.append('{}'.format(satir.val()))
                    csv_writer.writerow(yeni_satir)


            flash(f'{index} Convert İşlemi Başarılı',"success")




        # liste=[cikti]
        #
        # df = pd.DataFrame(liste)
        # p=r'C:/Users/LENOVO/Desktop/'
        # path=Path(p+index+'.csv')
        #
        # if os.path.exists(path) and path:
        #     flash(f"{index} dosyası zaten var","danger")
        # else:
        #     df.to_csv(path,header=False,index=False)
        #
        #     flash(f'{index} Convert İşlemi Başarılı',"success")


    except BaseException as e:
        print(f'Hata: {str(e)}')

    finally:
        return render_template('index.html')



if __name__ == '__main__':
    app.run(debug=True)