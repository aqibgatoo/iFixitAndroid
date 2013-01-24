package com.dozuki.ifixit.guide_create.ui;

import java.util.ArrayList;

import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.ToggleButton;

import com.dozuki.ifixit.MainApplication;
import com.dozuki.ifixit.R;
import com.dozuki.ifixit.guide_create.model.GuideCreateStepObject;
import com.dozuki.ifixit.guide_view.model.StepImage;
import com.ifixit.android.imagemanager.ImageManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;

public class GuideCreateStepListItem extends RelativeLayout {
   private static int ANIMATION_DURATION = 300;
   private TextView stepsView;
   private TextView stepNumber;
   private ToggleButton mToggleEdit;
   private TextView mDeleteButton;
   private TextView mEditButton;
   private LinearLayout mEditBar;
   private ImageView mImageView;
   private FrameLayout mStepFrame;
   private Context mContext;
   private ImageManager mImageManager;
   private GuideCreateStepPortalFragment mPortalRef;
   private GuideCreateStepObject mStepObject;
   private int mStepPosition;

   public GuideCreateStepListItem(Context context, ImageManager imageManager,
      final GuideCreateStepPortalFragment portalRef, GuideCreateStepObject sObject, int position) {
      super(context);
      mContext = context;
      mPortalRef = portalRef;
      mImageManager = imageManager;
      mStepObject = sObject;
      mStepPosition = position;
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      inflater.inflate(R.layout.guide_create_step_list_item, this, true);
      stepsView = (TextView) findViewById(R.id.step_title_textview);
      stepNumber = (TextView) findViewById(R.id.guide_create_step_item_number);
      mToggleEdit = (ToggleButton) findViewById(R.id.step_item_toggle_edit);
      mImageView = (ImageView) findViewById(R.id.guide_step_item_thumbnail);
      mDeleteButton = (TextView) findViewById(R.id.step_create_item_delete);
      mEditButton = (TextView) findViewById(R.id.step_create_item_edit);
      mEditBar = (LinearLayout) findViewById(R.id.step_create_item_edit_section);
      mStepFrame = (FrameLayout) findViewById(R.id.guide_step_edit_frame);
      boolean isEdit = sObject.getEditMode();
      mToggleEdit.setOnCheckedChangeListener(null);
      mToggleEdit.setChecked(isEdit);
      mToggleEdit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mStepObject.setEditMode(isChecked);
            portalRef.onItemSelected( mStepObject.getStepNum(), isChecked);
            setEditMode(isChecked, true, mToggleEdit, mEditBar);
         }
      });
      mStepFrame.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
            mToggleEdit.toggle();
         }
      });
      mDeleteButton.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
            mPortalRef.deleteStep(mStepObject);
            mPortalRef.invalidateViews();
            mPortalRef.verifyReorder();
         }
      });
      mEditButton.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
            mPortalRef.launchStepEdit(mStepPosition);
         }
      });
      String step = mStepObject.getTitle();
      stepsView.setText(step);
      stepNumber.setText("Step " + (mStepPosition + 1));
      setImageThumb(mStepObject.getImages(), mImageView);
      setEditMode(isEdit, false, mToggleEdit, mEditBar);
   }

   public void setEditMode(boolean isChecked, boolean animate, final ToggleButton mToggleEdit,
      final LinearLayout mEditBar) {
      if (isChecked) {
         if (animate) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_clockwise);

            mToggleEdit.startAnimation(rotateAnimation);

            // Creating the expand animation for the item
            ExpandAnimation expandAni = new ExpandAnimation(mEditBar, ANIMATION_DURATION);
            expandAni.setAnimationListener(new AnimationListener() {

               @Override
               public void onAnimationEnd(Animation animation) {
                  mPortalRef.invalidateViews();
               }

               @Override
               public void onAnimationRepeat(Animation animation) {
                  // TODO Auto-generated method stub

               }

               @Override
               public void onAnimationStart(Animation animation) {

               }
            });
            // Start the animation on the toolbar
            mEditBar.startAnimation(expandAni);
         } else {
            mEditBar.setVisibility(View.VISIBLE);
            ((LinearLayout.LayoutParams) mEditBar.getLayoutParams()).bottomMargin = 0;
         }

      } else {
         if (animate) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_counterclockwise);

            mToggleEdit.startAnimation(rotateAnimation);
            // Creating the expand animation for the item
            ExpandAnimation expandAni = new ExpandAnimation(mEditBar, ANIMATION_DURATION);
            // mPortalRef.invalidateViews();
            expandAni.setAnimationListener(new AnimationListener() {

               @Override
               public void onAnimationEnd(Animation animation) {
                  mPortalRef.invalidateViews();
                  // mStepList.requestLayout();
               }

               @Override
               public void onAnimationRepeat(Animation animation) {}

               @Override
               public void onAnimationStart(Animation animation) {

               }
            });
            // Start the animation on the toolbar
            mEditBar.startAnimation(expandAni);
         } else {
            mEditBar.setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) mEditBar.getLayoutParams()).bottomMargin = -50;
         }
      }
   }

   private void setImageThumb(ArrayList<StepImage> imageList, ImageView imagView) {

      for (StepImage imageinfo : imageList) {
         if (imageinfo.getImageid() > 0) {
            imagView.setScaleType(ScaleType.FIT_CENTER);
            mImageManager.displayImage(imageinfo.getText() + MainApplication.get().getImageSizes().getThumb(),
               mPortalRef.getActivity(), imagView);
            imagView.setTag(imageinfo.getText() + MainApplication.get().getImageSizes().getThumb());
            imagView.invalidate();
            return;
         }
      }

   }

   public void setChecked(boolean check) {
      mToggleEdit.setChecked(check);
   }

}