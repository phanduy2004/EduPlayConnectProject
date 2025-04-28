    package com.myjob.real_time_chat_final;

    import com.myjob.real_time_chat_final.modelDTO.CommentDTO;

    public interface OnCommentInteractionListener {
        void onLikeComment(CommentDTO comment);
        void onReplyComment(CommentDTO comment);
        void onShareComment(CommentDTO comment);
    }
