package com.buang.welewolf.modules.message;

import android.content.Context;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.adapter.MessageListAdapter;

/**
 * Created by weilei on 17/6/7.
 */

public class ConversationFragmentEx extends ConversationFragment {

    @Override
    public MessageListAdapter onResolveAdapter(Context context) {
        return new MessageAdapter(context);
    }
}
