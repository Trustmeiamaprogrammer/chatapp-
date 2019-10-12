package com.example.chatapp;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Extends veranderd naar implements
public abstract class EndlessRecyclerOnScrollListener implements View.OnClickListener {

    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
    private int vorigAantal = 0;
    private boolean laden = true;
    private int zichtbaarThreshold = 5;
    int eersteZichtbaarItem, zichtbaarItemCount, totaalItemCount;

    private int huidigPag = 1;

    private LinearLayoutManager linearLayoutManager;
    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager ) {
        this.linearLayoutManager = linearLayoutManager;

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
        super.onScrolled(recyclerView, dx, dy);

        zichtbaarItemCount = recyclerView.getChildCount();
        totaalItemCount = linearLayoutManager.getItemCount();

        eersteZichtbaarItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

        if(laden){
            if(totaalItemCount > vorigAantal){
                laden = false;
                vorigAantal = totaalItemCount;
            }
        }

        if (!laden && (totaalItemCount - zichtbaarItemCount) <= (eersteZichtbaarItem + zichtbaarThreshold) {
            huidigPag ++;
            laadMeer(huidigPag);
            laden = true;

        }
    }
    public abstract void laadMeer(int huidigPag);
}
