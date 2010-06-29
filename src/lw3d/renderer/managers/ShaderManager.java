package lw3d.renderer.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lw3d.renderer.ShaderProgram;
import lw3d.renderer.ShaderProgram.Shader;
import lw3d.renderer.ShaderProgram.Shader.Type;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderManager {
	
	Map<ShaderProgram, Integer> shaderProgramHandles = new HashMap<ShaderProgram, Integer>();
	
	final private boolean isUseFixedVertexFunction;
	
	public ShaderManager(boolean isUseFixedVertexFunction) {
		this.isUseFixedVertexFunction = isUseFixedVertexFunction;
	}
	
	public int getShaderProgramHandle(ShaderProgram shaderProgram) {
		if(tryToUpload(shaderProgram))
			return shaderProgramHandles.get(shaderProgram);
		else
			return 0;
	}
	
	private boolean tryToUpload(ShaderProgram shaderProgram) {
		if(shaderProgramHandles.containsKey(shaderProgram))
			return true;
		
		int shaderProgramHandle = ARBShaderObjects.glCreateProgramObjectARB();
		
		Iterator<ShaderProgram.Shader> it = shaderProgram.getShaders().iterator();
		while(it.hasNext()) {
			Shader shader = it.next();
			if(shader.type == Type.VERTEX && isUseFixedVertexFunction)
				continue;
			
			int shaderHandle = ARBShaderObjects.glCreateShaderObjectARB(shader.type.getValue());
						
			ARBShaderObjects.glShaderSourceARB(shaderHandle, shader.source);
			ARBShaderObjects.glCompileShaderARB(shaderHandle);
			
			ARBShaderObjects.glAttachObjectARB(shaderProgramHandle, shaderHandle);
			
			System.out.println("logShad: " +
					ARBShaderObjects.glGetInfoLogARB(shaderHandle, 2048));
			
		}
		
		ARBShaderObjects.glLinkProgramARB(shaderProgramHandle);
		
		System.out.println("logProg: " + 
				ARBShaderObjects.glGetInfoLogARB(shaderProgramHandle, 2048));
		
		ARBShaderObjects.glValidateProgramARB(shaderProgramHandle);
		System.out.println("validProgStat: " +
				ARBShaderObjects.glGetObjectParameteriARB(shaderProgramHandle,
						ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB));
		
		shaderProgramHandles.put(shaderProgram, shaderProgramHandle);
		
		return true;
	}

}
