package com.android.team.moshey.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.team.moshey.R;
import com.android.team.moshey.databinding.ActivityBookingBinding;
import com.android.team.moshey.models.entities.tickets.MyTicket;
import com.android.team.moshey.ui.adapters.BookTicketFirestoreAdapter;
import com.android.team.moshey.ui.adapters.callback.IBookTicketCallback;
import com.android.team.moshey.ui.adapters.callback.IFirestoreAdapterCallback;
import com.android.team.moshey.ui.viewmodels.BookingViewModel;
import com.android.team.moshey.ui.viewmodels.BookingViewModelFactory;
import com.android.team.moshey.utils.ConstantUtils;
import com.android.team.moshey.utils.InjectorUtils;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity implements IFirestoreAdapterCallback, IBookTicketCallback {

    private BookingViewModel mBookingViewModel;
    private ActivityBookingBinding mBookingBinding;
    private BookTicketFirestoreAdapter mBookTicketFirestoreAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TODO 8. Set Mode in Activity - offside recreates activity use for one off overrides
//        getDelegate().setLocalNightMode(
//                AppCompatDelegate.MODE_NIGHT_YES);
        mBookingBinding = DataBindingUtil.setContentView(this, R.layout.activity_booking);
        mBookingBinding.bookingAppBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        Toolbar vToolbar = mBookingBinding.bookingAppBar;
        vToolbar.setTitle(getString(R.string.book_title));
        setSupportActionBar(vToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        BookingViewModelFactory factory = InjectorUtils
                .provideDetailViewModelFactory(this.getApplicationContext());
        mBookingViewModel = ViewModelProviders.of(this, factory).get(BookingViewModel.class);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mBookTicketFirestoreAdapter = new BookTicketFirestoreAdapter(
                mBookingViewModel.getFirestoreRecyclerOptions(),
                this,
                this);
        mRecyclerView = mBookingBinding.availableTicketsRecycler;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBookTicketFirestoreAdapter != null)
            mBookTicketFirestoreAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBookTicketFirestoreAdapter != null)
            mBookTicketFirestoreAdapter.stopListening();
    }

    @Override
    public void onAdapterDataChanged(int itemCount) {
        if (!(itemCount > 0)) {
            Toast.makeText(
                    BookingActivity.this,
                    getString(R.string.no_new_tickets),
                    Toast.LENGTH_LONG)
                    .show();
        }
        mRecyclerView.setAdapter(mBookTicketFirestoreAdapter);
    }

    @Override
    public void bookTrainTicket(String to, String from) {
        MyTicket vMyTicket = generateTicket(to, from);
        mBookingViewModel.bookTicket(vMyTicket);
    }

    private MyTicket generateTicket(String to, String from) {
        String ticketID = "#".concat(ConstantUtils.generateTicketId(8));
        MyTicket vMyTicket = new MyTicket();
        vMyTicket.setTo(to);
        vMyTicket.setFrom(from);
        vMyTicket.setTicketId(ticketID);
        vMyTicket.setDate(ConstantUtils.buildCurrentDateString(Calendar.getInstance()));
        Log.d("Date is", vMyTicket.getDate());
        return vMyTicket;
    }
}
