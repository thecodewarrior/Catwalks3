package catwalks.util;

import java.util.HashMap;
import java.util.Map;

public class Trimap<A, B, C> {

	private Map<A, B> mapAtoB = new HashMap<>();
	private Map<A, C> mapAtoC = new HashMap<>();
	
	private Map<B, A> mapBtoA = new HashMap<>();
	private Map<B, C> mapBtoC = new HashMap<>();
	
	private Map<C, A> mapCtoA = new HashMap<>();
	private Map<C, B> mapCtoB = new HashMap<>();

	private Class<?> classA, classB, classC;
	
	public Trimap(Class<? extends A> aClass, Class<? extends B> bClass, Class<? extends C> cClass) {
		classA = aClass;
		classB = bClass;
		classC = cClass;
	}
	
	public void put(A a, B b, C c) {
		mapAtoB.put(a, b);
		mapAtoC.put(a, c);

		mapBtoA.put(b, a);
		mapBtoC.put(b, c);

		mapCtoA.put(c, a);
		mapCtoB.put(c, b);
	}
	
	public A getA(Object val) {
		if(classB.isAssignableFrom(val.getClass())) {
			return mapBtoA.get(val);
		}
		if(classC.isAssignableFrom(val.getClass())) {
			return mapCtoA.get(val);
		}
		return null;
	}
	
	
	public B getB(Object val) {
		if(classA.isAssignableFrom(val.getClass())) {
			return mapAtoB.get(val);
		}
		if(classC.isAssignableFrom(val.getClass())) {
			return mapCtoB.get(val);
		}
		return null;
	}
	
	
	public C getC(Object val) {
		if(classA.isAssignableFrom(val.getClass())) {
			return mapAtoC.get(val);
		}
		if(classB.isAssignableFrom(val.getClass())) {
			return mapBtoC.get(val);
		}
		return null;
	}
	
}
