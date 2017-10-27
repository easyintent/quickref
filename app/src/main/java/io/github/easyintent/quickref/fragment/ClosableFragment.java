package io.github.easyintent.quickref.fragment;


public interface ClosableFragment {

    /** Check whether this fragment can be closed.
     *  This method will be invoked when back button pressed.
     *
     * @return
     */
    boolean allowBack();
}
