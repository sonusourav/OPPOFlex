package com.sonusourav.oppoflex;

import android.os.Bundle;
import android.util.Log;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.sonusourav.oppoflex.activities.BaseActivity;

public class TestActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getLayoutInflater().inflate(R.layout.activity_choose, frameLayout);

    int sonu = Hashing.murmur3_32()
        .newHasher()
        .putString("son17/12/2020sbiedu", Charsets.UTF_8)
        .hash()
        .asInt();

    int aman = Hashing.murmur3_32()
        .newHasher()
        .putString("ama17/12/2020sbihome", Charsets.UTF_8)
        .hash()
        .asInt();

    int sonu1 = Hashing.murmur3_32()
        .newHasher()
        .putString("sonu17/12/2020sbiedu", Charsets.UTF_8)
        .hash()
        .asInt();

    int aman1 = Hashing.murmur3_32()
        .newHasher()
        .putString("aman17/12/2020sbihome", Charsets.UTF_8)
        .hash()
        .asInt();

    Log.d("TestActivity",Integer.toString(sonu));
    Log.d("TestActivity",Integer.toString(sonu1));
    Log.d("TestActivity",Integer.toString(aman));
    Log.d("TestActivity",Integer.toString(aman1));

  }
}
