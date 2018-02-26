package com.example.vibhore.subtitulo.data;

import android.provider.BaseColumns;

/**
 * Created by vibhore on 23/2/18.
 */
public class ProductContract {

    ProductContract(){}



    public static final class SignUpEntry implements BaseColumns{
        public static final String TABLE_NAME="sign_up";
        public static final String COLUMN_SIGNUP_NAME="name";
        public static final String COLUMN_SIGNUP_MAIL="mail";
        public static final String COLUMN_SIGNUP_ADDRESS="address";
        public static final String COLUMN_SIGNUP_PHONE="phone";
        public static final String COLUMN_SIGNUP_USERNAME="username";
        public static final String COLUMN_SIGNUP_PASSWORD="password";
    }

    public static final class CartEntry implements BaseColumns{

        public static final String TABLE_NAME="cart";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_CART_DESCRIPTION ="description";
        public static final String COLUMN_CART_COLOR ="rang";
        public static final String COLUMN_CART_PRICE ="price";
        public static final String COLUMN_CART_STOCK ="stock";

    }
}
