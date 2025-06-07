package com.hevttc.bigwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.widget.SearchView;
import com.hevttc.bigwork.Adapter.BookAdapter;
import com.hevttc.bigwork.Dao.BookDao;
import com.hevttc.bigwork.Dao.BorrowRecordDao;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.bean.Book;

import java.util.ArrayList;
import java.util.List;


public class BookBorrowActivity extends AppCompatActivity implements BookAdapter.OnItemClickListener {
    private BookAdapter adapter;
    private BookDao bookDao;
    private BorrowRecordDao borrowRecordDao;
    private String currentUserId;
    private RecyclerView rvBooks;
    private long lastClickTime = 0;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_borrow);

        loginManager = new LoginManager(this);
        // 检查用户是否登录
        if (!loginManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_book_borrow);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(v -> finish());

        // 初始化示例图书数据
        setupSampleBooks();

        currentUserId = loginManager.getCurrentStudentId();; // 示例ID
        // 检查用户是否登录
        if (!loginManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前页面
            return;
        }

        // 初始化DAO
        bookDao = new BookDao(this);
        borrowRecordDao = new BorrowRecordDao(this);

        // 设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_books);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Book> books = bookDao.getAllBooks();
        adapter = new BookAdapter(books, this, currentUserId); // 传入当前用户ID
        recyclerView.setAdapter(adapter);


    }
    private void filterBooks(String query) {
        new Thread(() -> {
            List<Book> filtered = new BookDao(this).searchBooks(query);
            runOnUiThread(() -> adapter.updateData(filtered));
        }).start();
    }
    @Override
    public void onBorrowClick(int position) {
        Book book = adapter.getItem(position);

        // 防抖处理
        if (SystemClock.elapsedRealtime() - lastClickTime < 500) return;
        lastClickTime = SystemClock.elapsedRealtime();

        // 禁用按钮
        adapter.disableButton(position);

        new Thread(() -> {
            boolean success = borrowRecordDao.borrowBook(currentUserId, book.getBookId());

            runOnUiThread(() -> {
                if (success) {
                    book.setStock(book.getStock() - 1);
                    Toast.makeText(this, "借阅成功", Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent("REFRESH_BORROW_RECORDS"));
                } else {
                    // 具体错误提示
                    String errorMsg = getBorrowErrorReason(book.getBookId());
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }
                adapter.notifyItemChanged(position);
                adapter.enableButton(position);
            });
        }).start();
    }

    private String getBorrowErrorReason(int bookId) {
        // 检查具体失败原因
        BookDao bookDao = new BookDao(this);
        Book currentBook = bookDao.getBookById(bookId);

        // 1. 检查库存
        if (currentBook.getStock() <= 0) {
            return "库存不足";
        }

        // 2. 检查是否已借
        BorrowRecordDao recordDao = new BorrowRecordDao(this);
        if (recordDao.hasUnreturnedRecord(currentUserId, bookId)) {
            return "您已有未归还的该图书";
        }

        return "借阅失败，请重试";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_search, menu);

        // 确保使用 AndroidX 的 SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // 设置搜索提示
        searchView.setQueryHint("输入书名或作者");

        // 设置搜索监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交（可选）
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 实时过滤数据
                filterBooks(newText);
                return true;
            }
        });

        return true; // 必须返回 true 显示菜单
    }

    private void setupSampleBooks() {
        BookDao bookDao = new BookDao(this);

        // 示例图书数据
        List<Book> sampleBooks = new ArrayList<>();
        sampleBooks.add(new Book(0, "Android开发艺术探索", "任玉刚", "9787111496156", 5));
        sampleBooks.add(new Book(0, "Kotlin实战", "Dmitry Jemerov", "9787111579934", 3));
        sampleBooks.add(new Book(0, "重构：改善既有代码的设计", "Martin Fowler", "9787115357611", 2));
        sampleBooks.add(new Book(0, "代码整洁之道", "Robert C. Martin", "9787121377963", 4));
        sampleBooks.add(new Book(0, "深入理解Java虚拟机", "周志明", "9787111421900", 3));
        sampleBooks.add(new Book(0, "Python编程：从入门到实践", "Eric Matthes", "9787115428028", 5));
        sampleBooks.add(new Book(0, "算法导论", "Thomas H. Cormen", "9787115221704", 2));

        // 插入数据库
        new Thread(() -> {
            for (Book book : sampleBooks) {
                if (!isBookExists(book)) {
                    bookDao.addBook(book);
                }
            }
            runOnUiThread(() -> {
                // 刷新列表
                List<Book> books = bookDao.getAllBooks();
                adapter.updateData(books);
            });
        }).start();
    }

    private boolean isBookExists(Book newBook) {
        List<Book> existingBooks = bookDao.getAllBooks();
        for (Book book : existingBooks) {
            if (book.getIsbn().equals(newBook.getIsbn())) {
                return true;
            }
        }
        return false;
    }
}