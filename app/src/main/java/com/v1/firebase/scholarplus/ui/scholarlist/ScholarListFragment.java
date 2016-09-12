package com.v1.firebase.scholarplus.ui.scholarlist;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.utils.Constants;

import java.util.ArrayList;

/**
 * Created by sekartanjung on 6/8/16.
 */
public class ScholarListFragment extends Fragment {
    private MenuItem mSearchAction,mFilterAction;
    private EditText edtSeach;
    private boolean isSearchOpened = false;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Scholarship, ScholarshipViewHolder>
            mFirebaseAdapter;
    private ActionSortAdapter mCustomAdapter;
    private RecyclerView mSchRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private View rootView;
    private int toggleCat = 1;
    private Button btnDn,btnLn;
    private ArrayList<Scholarship> tempSch,sch;
    public ScholarListFragment() {
        /* Required empty public constructor */
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static ScholarListFragment newInstance() {
        ScholarListFragment fragment = new ScholarListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_scholarlist_menu,menu);
//        mSearchAction = menu.findItem(R.id.action_search);
        mFilterAction = menu.findItem(R.id.action_filter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
//            case R.id.action_search:
//                handleMenuSearch();
//                return true;
            case R.id.action_filter:
                handleMenuFilter();
                return true;
            case R.id.action_orderByDifficult:
//                mLinearLayoutManager.setReverseLayout(true);
//                mLinearLayoutManager.setStackFromEnd(true);
                return true;
            case R.id.action_orderByDuedate:
//                indexScholarship(Constants.KEY_DUEDATE,getCatAsal());
                mCustomAdapter.orderBy(Constants.KEY_DUEDATE);
                return true;
            case R.id.action_orderByName:
//                indexScholarship(Constants.KEY_NAMA,getCatAsal());
                mCustomAdapter.orderBy(Constants.KEY_NAMA);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scholarship, container, false);

        mSchRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_active_lists);
        btnDn = (Button) rootView.findViewById(R.id.cat_instate);
        btnLn = (Button) rootView.findViewById(R.id.cat_overseas);
        mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        sch = new ArrayList<Scholarship>();
        tempSch = new ArrayList<Scholarship>();
//        mLinearLayoutManager.setReverseLayout(true);
//        mLinearLayoutManager.setStackFromEnd(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        btnDn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleCat == 0)
                    scholarshipDn();toggleCat=1;
            }
        });
        btnLn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleCat == 1)
                    scholarshipLn();toggleCat=0;
            }
        });
        scholarshipDn();

//        mCustomAdapter = new FirebaseRecyclerAdapter<Scholarship, ScholarshipViewHolder>(
//                Scholarship.class,
//                R.layout.single_active_list_cardview2,
//                ScholarshipViewHolder.class,
//                mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_BEASISWA)
//
//        ) {
//            @Override
//            protected void populateViewHolder(final ScholarshipViewHolder viewHolder,final Scholarship model, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                viewHolder.createdByUser.setText(model.getInstansi());
//
//
//                new DownLoadImageTask(viewHolder.photoBy).execute(model.getPhotopath());
//
//                final String itemId = this.getRef(position).getKey();
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(getActivity(),ScholarListDetailActivity.class);
//                        viewHolder.photoBy.buildDrawingCache();
//                        Bitmap bm= viewHolder.photoBy.getDrawingCache();
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bm.compress(Bitmap.CompressFormat.PNG, 10, baos);
//                        byte[] b = baos.toByteArray();
//
//                        i.putExtra(Constants.FIREBASE_PROPERTY_BEASISWA, model);
//                        i.putExtra(Constants.DETAIL_PHOTO,b);
//                        i.putExtra(Constants.KEY_ID,itemId);
//                        startActivity(i);
//                    }
//                });
//            }
//        };

        mSchRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSchRecyclerView.setAdapter(mCustomAdapter);


        return rootView;
    }
    protected void handleMenuFilter(){

        /* Create an instance of the dialog fragment and show it */

        DialogFragment dialog = FilterDialogFragment.newInstance(getCatAsal());
        dialog.setTargetFragment(this,1);
        dialog.show(getFragmentManager(), "FilterDialogFragment");

    }

    public void search(ArrayList<String> a){
        indexScholarship(null,Constants.VALUE_ASAL_DN);
        mCustomAdapter.orderSearch(a);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        mCustomAdapter.orderSearch(extras.getStringArrayList("input"));
    }

    private void scholarshipDn(){
        btnDn.setBackgroundResource(R.drawable.shape4);
        btnLn.setBackgroundResource(R.color.iron);
        Query ref= mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_BEASISWA);
        indexScholarship(null,Constants.VALUE_ASAL_DN);
    }
    private void scholarshipLn(){
        btnLn.setBackgroundResource(R.drawable.shape4);
        btnDn.setBackgroundResource(R.color.iron);
        Query ref= mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_BEASISWA);
        indexScholarship(null,Constants.VALUE_ASAL_LN);
    }
    protected void handleMenuSearch(){
        ActionBar action = ((AppCompatActivity)getActivity()).getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);





            //add the search icon in the action bar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_48dp,null));
            }
            else{
                mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_48dp));
            }

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
//            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//                        return true;
//                    }
//                    return false;
//                }
//            });
            edtSeach.addTextChangedListener(new GenericTextWatcher());

            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_clear_white_48dp,null));
            }
            else{
                mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_clear_white_48dp));
            }
            isSearchOpened = true;


        }

    }

    private void doSearch() {

    }

    private String getCatAsal(){
        String s = "";
        if (toggleCat == 1){
            s = Constants.VALUE_ASAL_DN;
        }
        else{
            s = Constants.VALUE_ASAL_LN;
        }
        return s;
    }
    @Override
    public void onStop() {
        super.onStop();
        mCustomAdapter.cleanup();


    }
    protected void indexScholarship(String order,String sort){
        Query ref;
        if (order == null){
            ref= mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_BEASISWA);
        }
        else{
            ref = mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_BEASISWA).orderByChild(order);
        }
        if (mCustomAdapter != null)
            mCustomAdapter.cleanup();
        mCustomAdapter = new ActionSortAdapter(
                getActivity(),
                Scholarship.class,
                R.layout.single_active_list_cardview2,
                ScholarshipViewHolder.class,
                ref,
                mProgressBar,
                sort

        );
        mSchRecyclerView.setAdapter(mCustomAdapter);
    }

    public static class ScholarshipViewHolder extends RecyclerView.ViewHolder {
        public TextView createdByUser;
        public TextView ipk;
        public TextView website,duedate;
        public ImageView photoBy;
        public View mView;

        public ScholarshipViewHolder(View v) {
            super(v);
            mView = v;
            photoBy = (ImageView) itemView.findViewById(R.id.created_by);
            createdByUser = (TextView)itemView.findViewById(R.id.text_view_created_by_user);
            ipk = (TextView)itemView.findViewById(R.id.ipkkuh);
            website = (TextView) itemView.findViewById(R.id.web_beasiswa);
            duedate = (TextView) itemView.findViewById(R.id.duedatess);
        }

    }
    private class GenericTextWatcher implements TextWatcher {

        private Query ref;
        private String mInput;
        private GenericTextWatcher() {

            
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            mInput = editable.toString();
            Log.d("input",mInput);
            if (mCustomAdapter != null) mCustomAdapter.cleanup();mCustomAdapter = null;
            if (mInput.equals("")){
                Log.d("gak ada input","ya benar");
                ref = mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_BEASISWA);

            }else{

                ref = mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_BEASISWA).orderByChild("instansi")
                        .startAt(mInput.toUpperCase()).endAt(mInput.toLowerCase() + "~");

            }
            mCustomAdapter = new ActionSortAdapter(
                    getActivity(),
                    Scholarship.class,
                    R.layout.single_active_list_cardview2,
                    ScholarshipViewHolder.class,
                    ref,
                    mProgressBar,
                    Constants.VALUE_ASAL_DN

            );

            mSchRecyclerView.setAdapter(mCustomAdapter);
        }
    }
}