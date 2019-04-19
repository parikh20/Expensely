package team16.cs307.expensetracker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchAdapter extends FirestoreRecyclerAdapter<SearchItem, SearchAdapter.ViewHolder> {
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


        }
    }
}

