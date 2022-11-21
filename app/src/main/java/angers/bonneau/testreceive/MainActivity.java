package angers.bonneau.testreceive;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    //initialisation des variable vue + firebase + message au cas ou dans la base l'element indiqué est nul
    private Button btn;
    private TextView editName,editMail,editCoursName,editCoursDesc, editCoursDuration;
    String currentName, currentMail, currentCoursName, currentCoursDesc, currentCoursDurantion;
    String message = "rien à afficher";

    FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //on récupère tout les élément de la vue dans des variable afin de set les valeur de la db
        btn = findViewById(R.id.btnRead);

        editName = findViewById(R.id.editName);
        editMail = findViewById(R.id.editMail);
        editCoursName = findViewById(R.id.editCoursName);
        editCoursDesc = findViewById(R.id.editCoursDesc);
        editCoursDuration = findViewById(R.id.editCoursDuration);
        //on met le btn de la vue sur écute si on clique dessus on lance la methode OnCick qui lance la methode readData
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readData();
            }
        });
    }

    private void readData() {
        //on récupère l'instance de la base
        fstore = FirebaseFirestore.getInstance();
        //on spécifie la collection concernant et demande de retourner les resultat
        //par id et par ordre décroissant on limite le résultat à 1 car on veut afficher le dernier truc saisie
       fstore.collection ("CoursInformation")
               .orderBy("id", Query.Direction.DESCENDING).limit(1)
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               //if ternaire si pour chaque élément (name/mail...) on récupère qq chose alors on lui met
                               //dans la variable qui lui correspond l'élement de la base sinon on lui met le message "rien à afficher"
                               currentName = (document.get("name").toString().length() >0) ? document.get("name").toString() : message;
                               currentMail = (document.get("mail").toString().length() >0) ? document.get("mail").toString() : message;
                               currentCoursName = (document.get("nomCours").toString().length() >0) ? document.get("nomCours").toString() : message;
                               currentCoursDesc = (document.get("descriptionCours").toString().length() >0) ? document.get("descriptionCours").toString() : message;
                               currentCoursDurantion = (document.get("dureeCours").toString().length() >0) ? document.get("dureeCours").toString() : message;
                                //on appel la fonction dataPrompt qui sert à afficher les résultat
                               dataPrompt(currentName,currentMail,currentCoursName,currentCoursDesc,currentCoursDurantion);

                           }
                       } else {
                           Log.d(TAG, "Error getting documents: ", task.getException());
                       }
                   }
               });

    }

    private void dataPrompt(String currentName, String currentMail, String currentCoursName, String currentCoursDesc, String currentCoursDurantion) {

        int currentCoursDurantionIsINt,hours,min;
        //ici on convertit simplement le nombre de minutes saise en heure et minutes
        //on set ensuite tout les textview de la page avec les datas
        if (!(currentCoursDurantion.equals(message))){
            currentCoursDurantionIsINt = Integer.parseInt(currentCoursDurantion);
            hours = currentCoursDurantionIsINt /60;
            min = currentCoursDurantionIsINt %60;
            editCoursDuration.setText(hours+"h "+min+"min");
        }else{
            editCoursDuration.setText(currentCoursDurantion);
        }

        editName.setText(currentName);
        editMail.setText(currentMail);
        editCoursName.setText(currentCoursName);
        editCoursDesc.setText(currentCoursDesc);


    }


}