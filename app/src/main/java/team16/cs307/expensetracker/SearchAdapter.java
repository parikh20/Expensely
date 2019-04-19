package team16.cs307.expensetracker;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchAdapter extends FirestoreRecyclerAdapter<SearchItem, SearchAdapter.ViewHolder> {

    private SearchAdapter.OnItemLongClickListener longlistener;

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<SearchItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position, @NonNull SearchItem model) {
        holder.Name.setText(model.getName());
        holder.Amount.setText("Amount: $ "+Double.toString(model.getAmount()));
        holder.Location.setText("Location: "+model.getLocation());
        holder.Priority.setText("Priority: "+ Integer.toString(model.getPriority()));
        if(model.isRepeating()){
            holder.repeating.setText("Repeating: Yes:");
        }else{
            holder.repeating.setText("Repeating: No");
        }
        Date epochTime = new Date(model.getTime()*1000L);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String finalTime = format.format(epochTime);

        holder.time.setText(finalTime);


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item,viewGroup,false);
        return new ViewHolder(v);
    }

//    public void deleteItem( int position){
//        getSnapshots().getSnapshot(position).getReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    FileOutputStream fos= null;
//                    try{
//
//                    }
//                }else{
//                    System.out.println("Faild\n");
//                }
//            }
//        });
//
//        }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Name;
        private TextView Priority;
        private TextView Amount;
        private TextView Location;
        private TextView repeating;
        private TextView time;



        public ViewHolder(View view) {
            super(view);

            Name = view.findViewById(R.id.Search_Description);
            Priority = view.findViewById(R.id.Search_Priority);
            Amount = view.findViewById(R.id.Search_amount);
            Location = view.findViewById(R.id.Search_location);
            repeating = view.findViewById(R.id.Search_repeating);
            time = view.findViewById(R.id.Search_time);
            Name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && longlistener != null){
                        longlistener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                    return false;
                }
            });


        }
    }


    public interface OnItemLongClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }




    public void setLongClickListener(SearchAdapter.OnItemLongClickListener longlistener){
        this.longlistener=longlistener;
    }



}

