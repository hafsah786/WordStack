/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private char [] bufferArr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                if (word.length() == WORD_LENGTH){
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        word1LinearLayout.setOnTouchListener(new TouchListener());
        //word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        word2LinearLayout.setOnTouchListener(new TouchListener());
        //word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                stackedLayout.push(tile);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }


                    //stackedLayout.push(tile);
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        String buffer = "";
        messageBox.setText("Game started");
        int rand = random.nextInt(words.size());



        word1 = words.get(rand);
        char[] word1Arr = word1.toCharArray();
        rand = random.nextInt(words.size());
        word2 = words.get(rand);
        char[] word2Arr = word2.toCharArray();

        int counter1 = 0;
        int counter2 = 0;


        while (counter1 < WORD_LENGTH|| counter2 < WORD_LENGTH){
             int r = random.nextInt(2) +1;

             if (r == 1 && counter1 < WORD_LENGTH){
                 int n = counter1 ;
                 //buffer += word1.substring(counter1, n+2);
                 buffer += word1Arr[counter1];
                 counter1++;
                 System.out.print("1 "+ n);

             }
             else if (r == 2 && counter2 < WORD_LENGTH){
                 int n = counter2;
                 //buffer += word2.substring(counter2, n+2);
                 buffer += word1Arr[counter2];
                 counter2++;
                 System.out.print("2 "+ n);
             }
             System.out.println(r);
            System.out.println("buffer "+buffer);

        }

        bufferArr = buffer.toCharArray();
        System.out.println(buffer);
        for (int i = buffer.length()-1; i >= 0; i--){
            //View letter  = (View) buffer.substring(i, i-1);
            LetterTile character = new LetterTile(this, bufferArr[i]);
            View c = (View) character;
            stackedLayout.push(c);
           // System.out.println(bufferArr[i]);
        }

        messageBox.setText(buffer);

        return true;

    }

    public boolean onUndo(View view) {
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        ViewGroup word1LinearLayout = (ViewGroup) findViewById(R.id.word1);
        ViewGroup word2LinearLayout = (ViewGroup)findViewById(R.id.word2);

        savedInstanceState.putInt(word1, word1.length());
        savedInstanceState.putInt(word2,word2.length());


        int count1 = 0 ;
        String lettersPlayer1 = "";

        if (word1 != null){
            savedInstanceState.putString("word1", word1);
        }

        if (word2 != null){
            savedInstanceState.putString("word2", word2);
        }

        if(word1LinearLayout != null) {
            while (count1 != word1LinearLayout.getChildCount()) {
                LetterTile current = (LetterTile) word1LinearLayout.getChildAt(count1);
                String currentLetter = current.getLetter();
                lettersPlayer1 += currentLetter;
                count1++;

            }
            savedInstanceState.putString("word1LinearLayout", lettersPlayer1);
        }

        int count2 = 0 ;
        String lettersPlayer2 = "";

        if(word2LinearLayout != null) {

            while (count2 != word2LinearLayout.getChildCount()) {
                LetterTile current = (LetterTile) word2LinearLayout.getChildAt(count1);
                String currentLetter = current.getLetter();
                lettersPlayer2 += currentLetter;
                count2++;

            }
            savedInstanceState.putString("word2LinearLayout", lettersPlayer2);
        }



            Stack<View> getTiles = stackedLayout.getTiles();
            if(!getTiles.isEmpty()) {
            String stackLetter = "";
            int countStack = 0;

            while (countStack != getTiles.size() ) {
                LetterTile current = (LetterTile) stackedLayout.pop();
                String currentLetter = current.getLetter();
                stackLetter += currentLetter;
                countStack++;

            }
            savedInstanceState.putString("stackedLayout", stackLetter);
        }



        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        ViewGroup word1LinearLayout = (ViewGroup) findViewById(R.id.word1);
        ViewGroup word2LinearLayout = (ViewGroup) findViewById(R.id.word2);
        String word1LL = savedInstanceState.getString("word1LinearLayout");
        String word2LL = savedInstanceState.getString("word2LinearLayout");
        String stackLayout = savedInstanceState.getString("stackedLayout");


        if (savedInstanceState.getString("word1") != null ){
            this.word1 = savedInstanceState.getString("word1");
        }
        if (savedInstanceState.getString("word2") != null ){
            this.word2 = savedInstanceState.getString("word2");
        }

        if (savedInstanceState.getString("word1LinearLayout") != null ){
            for (int i =0; i < word1LL.length(); i++){
               LetterTile letter =  new LetterTile(this, word1LL.charAt(i));
               word1LinearLayout.addView(letter);

            }
        }

        if (savedInstanceState.getString("word2LinearLayout") != null ){
            for (int i =0; i < word2LL.length(); i++){
                LetterTile letter =  new LetterTile(this, word2LL.charAt(i));
                word2LinearLayout.addView(letter);
            }
        }

        if (savedInstanceState.getString("stackedLayout") != null ){
            for (int i =0; i < stackLayout.length(); i++){
                LetterTile letter =  new LetterTile(this, stackLayout.charAt(i));
                stackedLayout.push(letter);
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }
}


