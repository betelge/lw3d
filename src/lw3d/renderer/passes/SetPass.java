package lw3d.renderer.passes;

import org.lwjgl.opengl.GL11;

public class SetPass extends RenderPass {
	
	State state;
	boolean set;
	
	public SetPass(State state, boolean set) {
		this.state = state;
		this.set = set;
	}
	
	public enum State {
		DEPTH_TEST(GL11.GL_DEPTH_TEST), DEPTH_WRITE(0);
		
		int value;
		
		State(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public State getState() {
		return state;
	}
	
	public boolean isSet() {
		return set;
	}
}
