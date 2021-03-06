package com.gdxengine.framework.camera;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gdxengine.framework.Utils;

public class ChaseCamera extends PerspectiveCamera {

	 public Vector3 AvatarHeadOffset;
     public Vector3 TargetOffset;

     float lastY;
     Vector3 lastPosition;
     float time = 0f;
     float chaseSpeed = 2f;
     float interpolatedSpeed = 0f;
     
     public ChaseCamera(float fieldOfView, float viewportWidth, float viewportHeight)
     {
    	 super(fieldOfView, viewportWidth, viewportHeight);
    	 //if(Game.isSupportOpenGL20)
    	 {
	         AvatarHeadOffset = new Vector3(0, 2f, -3.5f);
	         TargetOffset = new Vector3(0, -1, 3);
    	 }

         view.idt();
         
         lastPosition = new Vector3(position);
         
         float aspect = viewportWidth / viewportHeight;
  		projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
     }
     
     @Override
     public void update(){
 		combined.set(projection);
		Matrix4.mul(combined.val, view.val);

		invProjectionView.set(combined);
		Matrix4.inv(invProjectionView.val);
		frustum.update(invProjectionView);
     }

     
     public void Update(float y, Vector3 vectorUp, Vector3 position, float elapsedTime)
     {
    	 this.up.set(vectorUp);
         boolean needResetTie = false;
         time += elapsedTime;
         if (y != lastY)
         {
             interpolatedSpeed = MathUtils.clamp(chaseSpeed * elapsedTime, 0.0f, 1.0f);
             y = Utils.Lerp(lastY, y, interpolatedSpeed);
         }
         else
         {
             needResetTie = true;
         }
        
         if (lastPosition != position)
         {
             interpolatedSpeed = MathUtils.clamp(chaseSpeed * elapsedTime, 0.0f, 1.0f);
             this.position.set(Utils.Lerp(lastPosition, position, interpolatedSpeed));
         }
         else
         {
             needResetTie = true;
         }
         if (needResetTie)
         {
             time = 0f;
         }
         
         Matrix4 rotationMatrix = new Matrix4();
         rotationMatrix.setToRotation(0, 1, 0, y);
         Vector3 transformedheadOffset = Utils.Transform(AvatarHeadOffset, rotationMatrix);
         Vector3 transformedReference = Utils.Transform(TargetOffset, rotationMatrix);

         Vector3 cameraPosition = Utils.addVector(this.position, transformedheadOffset);
         Vector3 cameraTarget = Utils.addVector(this.position , transformedReference);
         
         vectorUp  = Utils.Transform(vectorUp, rotationMatrix);

         lastY = y;
         lastPosition = this.position;
         
         view.setToLookAt(cameraPosition, cameraTarget, vectorUp);
     }
 }
