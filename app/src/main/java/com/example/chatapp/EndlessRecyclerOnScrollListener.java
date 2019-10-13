package com.example.chatapp;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

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
    public void onScrolled(RecyclerView Recyclerview, int dx, int dy){
        super.onScrolled(Recyclerview, dx, dy);

        zichtbaarItemCount = Recyclerview.getChildCount();
        totaalItemCount = linearLayoutManager.getItemCount();

        eersteZichtbaarItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

        if(laden){
            if(totaalItemCount > vorigAantal){
                laden = false;
                vorigAantal = totaalItemCount;
            }
        }

        if (!laden && (totaalItemCount - zichtbaarItemCount)
                <= (eersteZichtbaarItem + zichtbaarThreshold)){

            huidigPag ++;
            onlaadMeer(huidigPag);
            laden = true;

        }
    }
    public abstract void onlaadMeer(int huidigPag);
}
