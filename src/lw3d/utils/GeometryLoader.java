package lw3d.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.BufferUtils;

import lw3d.math.Vector3f;
import lw3d.renderer.Geometry;
import lw3d.renderer.Geometry.Attribute;
import lw3d.renderer.Geometry.Type;

public class GeometryLoader {

	static public Geometry loadObj(File file) {

		ArrayList<Vector3f> v = new ArrayList<Vector3f>();
		ArrayList<Vector3f> vt = new ArrayList<Vector3f>();
		ArrayList<Vector3f> vn = new ArrayList<Vector3f>();
		
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Float> v2 = new ArrayList<Float>();
		ArrayList<Float> vt2 = new ArrayList<Float>();
		ArrayList<Float> vn2 = new ArrayList<Float>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			int i = 0;
			while (br.ready()) {
				String line = br.readLine().trim();

				String[] words = line.split(" ");
				if(words[0].equals("v")) {
					v.add(new Vector3f(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3])));
				}
				else if(words[0].equals("vt")) {
					vt.add(new Vector3f(Float.parseFloat(words[1]), Float.parseFloat(words[2]), 0f));
				}
				else if(words[0].equals("vn")) {
					vn.add(new Vector3f(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3])));
				}
				else if(words[0].equals("f")) {
					for(int j = 1; j < 4; j++) {
						String[] letters = words[j].split("/");
						indices.add(3*i+j-1);
						
						Vector3f vec;
						if(!v.isEmpty()) {
							vec = v.get(Integer.parseInt(letters[0]) - 1);
							v2.add(vec.x);
							v2.add(vec.y);
							v2.add(vec.z);
							System.out.println(vec);
						}
						
						if(!vt.isEmpty()) {
							vec = vt.get(Integer.parseInt(letters[1]) - 1);
							vt2.add(vec.x);
							vt2.add(vec.y);
						}
						
						if(!vn.isEmpty()) {
							vec = vn.get(Integer.parseInt(letters[2]) - 1);
							vn2.add(vec.x);
							vn2.add(vec.y);
							vn2.add(vec.z);
						}
						
						i++;
					}					
				}
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size());
		Iterator<Integer> intIt = indices.iterator();
		while(intIt.hasNext())
			indexBuffer.put(intIt.next());
		indexBuffer.flip();
		
		if(!v2.isEmpty()) {
			Attribute at = new Attribute();
			at.buffer = BufferUtils.createFloatBuffer(v2.size());
			at.name = "position";
			at.size = 3;
			at.type = Type.FLOAT;
			
			Iterator<Float> fIt = v2.iterator();
			while(fIt.hasNext())
				((FloatBuffer) at.buffer).put(fIt.next());
			at.buffer.flip();
			
			attributes.add(at);
		}
		
		if(!vt2.isEmpty()) {
			Attribute at = new Attribute();
			at.buffer = BufferUtils.createFloatBuffer(v2.size());
			at.name = "textureCoord";
			at.size = 2;
			at.type = Type.FLOAT;
			
			Iterator<Float> fIt = vt2.iterator();
			while(fIt.hasNext())
				((FloatBuffer) at.buffer).put(fIt.next());
			at.buffer.flip();
			
			attributes.add(at);
		}
		
		if(!vn2.isEmpty()) {
			Attribute at = new Attribute();
			at.buffer = BufferUtils.createFloatBuffer(vn2.size());
			at.name = "normal";
			at.size = 3;
			at.type = Type.FLOAT;
			
			Iterator<Float> fIt = vn2.iterator();
			while(fIt.hasNext())
				((FloatBuffer) at.buffer).put(fIt.next());
			at.buffer.flip();
			
			attributes.add(at);
		}
		
		Geometry geometry = new Geometry(indexBuffer, attributes);
		
		return geometry;
	}
}
