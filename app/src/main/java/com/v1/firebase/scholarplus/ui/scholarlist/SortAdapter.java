package com.v1.firebase.scholarplus.ui.scholarlist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.utils.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by MacBook on 9/4/16.
 */
public abstract class SortAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    Class<T> mModelClass;
    protected int mModelLayout;
    Class<VH> mViewHolderClass;
//    FirebaseArray mSnapshots;
    private ArrayList<Scholarship> mModels;
    private ArrayList<Scholarship> mTemp;
    private ArrayList<String> mKeys;
    private Query mRef;
    private ChildEventListener mListener;
    private String mSort;

    /**
     * @param modelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>
     */
    public SortAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, Query ref,String sort) {
        mRef = ref;
        mModelClass = modelClass;
        mModelLayout = modelLayout;
        mViewHolderClass = viewHolderClass;
        mModels = new ArrayList<Scholarship>();
        mTemp = new ArrayList<Scholarship>();
        mKeys = new ArrayList<String>();
        mSort = sort;

        mListener = mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Scholarship model = dataSnapshot.getValue(Scholarship.class);
                String key = dataSnapshot.getKey();
                Scholarship data = dataSnapshot.getValue(Scholarship.class);
                // Insert into the correct location, based on previousChildName
                if (s == null) {
                    if(data.getAsal().equalsIgnoreCase(mSort)){
                        mModels.add(0, model);
                        mTemp.add(0, model);
                        mKeys.add(0, key);
                    }
                } else {
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        if(data.getAsal().equalsIgnoreCase(mSort)){
                            mModels.add(model);
                            mTemp.add(model);
                            mKeys.add(key);
                        }
                    } else {
                        if (data.getAsal().equalsIgnoreCase(mSort)){
                            mModels.add(nextIndex, model);
                            mTemp.add(nextIndex, model);
                            mKeys.add(nextIndex, key);
                        }
                    }
                }
//                mTemp = mModels;
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Scholarship newModel = dataSnapshot.getValue(Scholarship.class);
                int index = mKeys.indexOf(key);

                mModels.set(index, newModel);
                mTemp.set(index,newModel);
//                mTemp = mModels;
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            // A model was removed from the list. Remove it from our list and the name mapping
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                mModels.remove(index);
                mTemp.remove(index);
//                mTemp = mModels;
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            // A model changed position in the list. Update our list accordingly
                String key = dataSnapshot.getKey();
                Scholarship newModel = dataSnapshot.getValue(Scholarship.class);
                int index = mKeys.indexOf(key);
                mModels.remove(index);
                mTemp.remove(index);
                mKeys.remove(index);
                if (s == null) {
                    mModels.add(0, newModel);
                    mTemp.add(0,newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                        mTemp.add(newModel);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, newModel);
                        mTemp.add(nextIndex,newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
//                mTemp = mModels;
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mTemp = mModels;


//        mSnapshots.setOnChangedListener(new FirebaseArray.OnChangedListener() {
//            @Override
//            public void onChanged(EventType type, int index, int oldIndex) {
//                switch (type) {
//                    case Added:
//                        notifyItemInserted(index);
//                        break;
//                    case Changed:
//                        notifyItemChanged(index);
//                        break;
//                    case Removed:
//                        notifyItemRemoved(index);
//                        break;
//                    case Moved:
//                        notifyItemMoved(oldIndex, index);
//                        break;
//                    default:
//                        throw new IllegalStateException("Incomplete case statement");
//                }
//            }
//
//
//        });
    }

    /**
     * @param modelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>
     */
    public SortAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, DatabaseReference ref,String sort) {
        this(modelClass, modelLayout, viewHolderClass, (Query) ref,sort);
    }

    public void orderBy(String s){

        for (int i=0;i < mModels.size()-1;i++){
            if (s.equals(Constants.KEY_NAMA) && mModels.get(i).getNama().compareTo(mModels.get(i+1).getNama()) > 0)
            {

                Swap(i, i+1);

            }
            else if (s.equals(Constants.KEY_DUEDATE) &&  Constants.compareDate(mModels.get(i).getDuedate(),mModels.get(i+1).getDuedate()) < 0)
            {

                Swap(i, i+1);

            }
//            else if (mModels.get(i).getNama().compareTo(mModels.get(i+1).getNama()) > 0)
//            {
//                Swap(i, i+1);
//
//            }

        }

        notifyDataSetChanged();
    }
    private void Swap( int left, int right)
    {
        if (left != right)
        {
            Scholarship temp = mModels.get(left);
            mModels.set(left,mModels.get(right));
            mModels.set(right,temp);}
    }

    public void cleanup() {
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
    }
    public void orderSearch(ArrayList<String> data){

        if (!mTemp.equals(mModels))
        {mModels.clear();
            for (int i=0;i<mTemp.size();i++){
                mModels.add(mTemp.get(i));
            }


        }
        mTemp.clear();
//        ArrayList<Scholarship> a = new ArrayList<>();
//        a = mTemp;
        String dataSearch = data.get(0);
        Double dataIpk = 0.0;
        String dataJenis = "semua";
        if (!data.get(1).isEmpty())
            dataIpk = Double.parseDouble(data.get(1));
        int dataSemester = 0;
        if (!data.get(2).isEmpty())
            dataSemester = Integer.parseInt(data.get(2));
        if (!data.get(3).equalsIgnoreCase("semua"))
            dataJenis = data.get(3).toString();
        if (!dataSearch.isEmpty()){
            for (int i=0;i < mModels.size();i++) {
                mTemp.add(mModels.get(i));
                String mNama = mModels.get(i).getNama();
                String mIpk = mModels.get(i).getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString();
                String mSemester = mModels.get(i).getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString();
                if (!mNama.toLowerCase().contains(dataSearch.toLowerCase()) || dataIpk > Double.parseDouble(mIpk) || dataSemester > Integer.parseInt(mSemester)) {
                    Log.d("removed",mModels.get(i).getNama());
                    mModels.remove(i);
                    i--;

                }
            }

        }
        else{
            for (int i=0;i < mModels.size();i++) {
                mTemp.add(mModels.get(i));
                String mIpk = mModels.get(i).getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString();
                String mSemester = mModels.get(i).getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString();
                String mTipe = mModels.get(i).getJenis();
                if (dataIpk > Double.parseDouble(mIpk) || dataSemester > Integer.parseInt(mSemester) || (dataJenis.equalsIgnoreCase(mTipe) && !dataJenis.equalsIgnoreCase("semua"))) {
                    Log.d("removed",mModels.get(i).getNama()+" "+dataJenis);
                    mModels.remove(i);
                    i--;

                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public Scholarship getItem(int position) {
        return mModels.get(position);
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected T parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(mModelClass);
    }

    public DatabaseReference getRef(int position) {
//        return mSnapshots.getItem(position).getRef();
        return mRef.getRef();
    }

    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return position;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        Scholarship model = getItem(position);
        populateViewHolder(viewHolder, model, position);
    }

    @Override
    public int getItemViewType(int position) {
        return mModelLayout;
    }

    /**
     * This method will be triggered in the event that this listener either failed at the server,
     * or is removed as a result of the security and Firebase Database rules.
     *
     * @param databaseError A description of the error that occurred
     */

    protected void onCancelled(DatabaseError databaseError) {
        Log.w("FirebaseRecyclerAdapter", databaseError.toException());
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param model      The object containing the data used to populate the view
     * @param position  The position in the list of the view being populated
     */
    abstract protected void populateViewHolder(VH viewHolder, Scholarship model, int position);
}