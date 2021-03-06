package net.dongjian.meet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dongjian.framwork.base.BaseUIActivity;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.SpUtils;

import net.dongjian.meet.MainActivity;
import net.dongjian.meet.R;

import static com.dongjian.framwork.entity.Constants.SP_TOKEN;

/**
 * 启动页
 * 1、启动页全屏---用res-values里的styles.xml来做，在Manifest里引入主体即可
 * 2、延迟进入主页---用Handler来做
 * 3、根据具体逻辑判断是进入主页还是引导页还是登录页
 */
public class IndexActivity extends BaseUIActivity {

    private static final int SKIP_MAIN = 1000;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case SKIP_MAIN:
                    startMain();
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        //2、延迟进入主页
        mHandler.sendEmptyMessageDelayed(SKIP_MAIN,2 * 1000);

    }

    /**
     * 3、根据具体逻辑判断是进入主页还是引导页还是登录页
     */
    private void startMain(){
        //1、判断App是否是第一次启动 ： 即安装后第一次打开
        Boolean isFirstApp = SpUtils.getInstance().getBoolean(Constants.SP_IS_FIRST_AP,true);
        Intent intent = new Intent();
        //2、如果是第一次，那么跳转到引导页
        if(isFirstApp){
            //跳转到引导页
            intent.setClass(this,GuideActivity.class);
            //跳转之后就不是第一次打开了，所以需要设置一下
            SpUtils.getInstance().putBoolean(Constants.SP_IS_FIRST_AP,false);
        }else{
            //不是第一次启动，就通过token来判断是否曾经登录过
            String token = SpUtils.getInstance().getString(Constants.SP_TOKEN,"");
            if(TextUtils.isEmpty(token)){
                //特殊情况：第一次登录不会有token，所以这里还要通过BmobManager来判断是否是登录状态
                if (BmobManager.getmInstance().isLogin()) {
                    //跳转到主页：主页会再去检查token
                    intent.setClass(this, MainActivity.class);
                } else {
                    //跳转到登录页
                    intent.setClass(this, LoginActivity.class);
                }
            }else{
                //登陆过，就跳转到主页
                intent.setClass(this, MainActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }
}
