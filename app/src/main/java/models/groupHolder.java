package models;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xsmil.grouper.CurrentProjActivity;
import com.example.xsmil.grouper.HasAuthActivity;
import com.example.xsmil.grouper.MainActivity;
import com.example.xsmil.grouper.R;
import com.example.xsmil.grouper.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class groupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private View mView;
    private Context mContext;

    private String groupID;

    private final TextView mCourse;
    private final TextView mProjectTitle;
    private final TextView mProjectDeadline;
    private final RelativeLayout mGroupContainer;
    private final LinearLayout mGroup;

    public groupHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        mCourse = itemView.findViewById(R.id.course);
        mProjectTitle = itemView.findViewById(R.id.projectTitle);
        mProjectDeadline = itemView.findViewById(R.id.projectDeadline);
        mGroupContainer = itemView.findViewById(R.id.group_container);
        mGroup = itemView.findViewById(R.id.group);

        itemView.setOnClickListener(this);
        mGroupContainer.setMotionEventSplittingEnabled(false); // prevents activity from loading more than once when a list item is clicked on multiple times in succession
    }

    public void bind(@NonNull projectGroup group) {
        setCourse(group.course);
        setProjectTitle(group.projectTitle);
        setProjectDeadline(group.getProjectDeadline());
        groupID = group.groupID;
    }

    private void setCourse(@Nullable String course) {
        mCourse.setText(course);
    }

    private void setProjectTitle(@Nullable String projectTitle) {
        mProjectTitle.setText(projectTitle);
    }

    private void setProjectDeadline(@Nullable String projectDeadline) {
        mProjectDeadline.setText(projectDeadline);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, HasAuthActivity.class);
        intent.putExtra("groupID", groupID);
        mContext.startActivity(intent);
    }
}