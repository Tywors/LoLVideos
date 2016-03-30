package com.tywors.lolvideos.utils;

import android.widget.AbsListView;

/**
 * Created by W7 on 18/01/2015.
 */
public abstract class ListViewScroll implements AbsListView.OnScrollListener {
    //El minimo de elementos a tener debajo de la posicion actual del scroll antes de cargar mas
    private int visibleThreshold = 1;
    // La pagina actual
    private int currentPage = 0;
    // El total de elementos despues de la ultima carga de datos
    private int previousTotalItemCount = 0;
    //True si sigue esperando que termine de cargar el ultimo grupo de datos solicitados
    private boolean loading = true;
    // Sirve para setear la pagina inicial
    private int startingPageIndex = 0;

    public ListViewScroll() {
    }

    public ListViewScroll(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public ListViewScroll(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    /**
     * Ojo! Este metodo ocurrirá muchas veces por segundo durante un desplazamiento o scroll de la
     * lista, asi que, es de tener mucho cuidado lo que se escribe aquí.
     * El listener te provee de varios parametros útiles por si necesitas cargar más datos
     * pero, primero debes comprobar que una carga no esté en proceso si no tendrás un pequeño
     * infierno en tus manos.
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (!loading && (totalItemCount < previousTotalItemCount)) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading) {
            if (totalItemCount > previousTotalItemCount) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }
        }

        //Si hemos llegado al final de la lista y una carga de datos no esta en proceso
        //invoco el metodo onLoadMore para poder cargar mas datos.
        if (!loading
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
            loading = true;
        }
    }

    // Este metodo es abstracto para que el desarrollador defina la forma en que quiere
    // llamar más informacion, como parámetros recibirá la página actual y la cantidad
    // elementos cargados en la lista hasta el momento.
    public abstract void onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }
}