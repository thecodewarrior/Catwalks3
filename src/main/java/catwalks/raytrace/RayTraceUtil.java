package catwalks.raytrace;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import catwalks.shade.ccl.vec.Matrix4;
import net.minecraft.util.math.Vec3d;

public class RayTraceUtil {
	
	public static <T, R> ITraceResult<R> trace(Vec3d start, Vec3d end, List<? extends ITraceable<T, R>> traces, T param) {
		@SuppressWarnings("unchecked")
		ITraceResult<R> result = (ITraceResult<R>) MISS_RESULT;
		
		for (ITraceable<T, R> traceable : traces) {
			ITraceResult<R> hit = traceable.trace(start, end, param);
			result = min(result, hit);
		}
		
		return result;
	}
	
	public static <R> ITraceResult<R> min(ITraceResult<R> a, ITraceResult<R> b) {
		if(a == null && b == null)
			return null;
		if(a == null && b != null)
			return b;
		if(a != null && b == null)
			return a;
		
		if(b.hitDistance() < a.hitDistance()) {
			return b;
		}
		
		return a; // prefers a over b if distances are equal
	}
	
	public static ITraceResult<?> minNoGeneric(ITraceResult<?> a, ITraceResult<?> b) {
		if(a == null && b == null)
			return null;
		if(a == null && b != null)
			return b;
		if(a != null && b == null)
			return a;
		
		if(b.hitDistance() < a.hitDistance()) {
			return b;
		}
		
		return a; // prefers a over b if distances are equal
	}
	
	public static <T> IRenderableTraceResult<T> miss(T object) {
		return new IRenderableTraceResult<T>() {

			@Override
			public double hitDistance() {
				return Double.POSITIVE_INFINITY;
			}

			@Override
			public Vec3d hitPoint() {
				return Vec3d.ZERO;
			}

			@Override
			public T data() {
				return null;
			}
			
			@Override
			public List<VertexList> getVertices() {
				return ImmutableList.of();
			}
			
		};
	}
	
	{ /* interfaces */ }
	
	public static interface ITraceable<T, R> {
		@Nullable
		public ITraceResult<R> trace(Vec3d start, Vec3d end, T param);
	}
	
	public static abstract class TraceablePrimitive<T> implements ITraceable<Object, T> {
		public ITraceResult<T> trace(Vec3d start, Vec3d end, Object param) { return trace(start, end); }
		public abstract IRenderableTraceResult<T> trace(Vec3d start, Vec3d end);
		public abstract TraceablePrimitive<T> clone();
		public abstract Vec3d[] points();
		public abstract Vec3d[] edges();
		public abstract void rotate(int yRotation);
		public abstract void translate(Vec3d vec);
		public abstract void apply(Matrix4 matrix);
	}
	
	public static interface ITraceResult<T> {
		public double hitDistance();
		@Nonnull
		public Vec3d hitPoint();
		@Nullable
		public T data();
	}
	
	public static interface IRenderableFace {
		public List<VertexList> getVertices();
	}
	
	public static interface IRenderableTraceResult<T> extends ITraceResult<T>, IRenderableFace {}
	
	public static class VertexList {
		public Vec3d[] vertices;
		public boolean shouldHaveNetting;
		
		public VertexList(boolean shouldHaveNetting, Vec3d... vertices) {
			this.shouldHaveNetting = shouldHaveNetting;
			this.vertices = vertices;
		}
	}
	
	{ /* classes */ }
	
	public static class SimpleTraceResult<T> implements ITraceResult<T> {

		protected T data;
		protected Vec3d hit;
		protected double dist;
		
		public SimpleTraceResult(ITraceResult<?> other, T data) {
			dist = other.hitDistance();
			hit = other.hitPoint();
			this.data = data;
		}
		
		public SimpleTraceResult(Vec3d start, Vec3d hit, T data) {
			dist = hit.subtract(start).lengthVector();
			this.hit = hit;
			this.data = data;
		}
		
		@Override
		public double hitDistance() {
			return dist;
		}

		@Override
		public Vec3d hitPoint() {
			return hit;
		}

		@Override
		public T data() {
			return data;
		}
		
	}
	
	public static class SimpleRenderableTraceResult<T> extends SimpleTraceResult<T> implements IRenderableTraceResult<T> {

		private List<VertexList> vertices;

		public SimpleRenderableTraceResult(ITraceResult<?> other, T data, List<VertexList> vertices) {
			super(other, data);
			this.vertices = vertices;
		}
		
		public SimpleRenderableTraceResult(Vec3d start, Vec3d hit, T data, List<VertexList> vertices) {
			super(start, hit, data);
			this.vertices = vertices;
		}

		@Override
		public List<VertexList> getVertices() {
			return vertices;
		}
	}
	
	public static final ITraceResult<?> MISS_RESULT = new ITraceResult<Object>() {

		@Override
		public double hitDistance() {
			return Double.POSITIVE_INFINITY;
		}

		@Override
		public Vec3d hitPoint() {
			return Vec3d.ZERO;
		}

		@Override
		public Object data() {
			return null;
		}
		
	};
	
	public static final TraceablePrimitive<Object> NULL_TRACABLE = new TraceablePrimitive<Object>() {

		@Override
		public IRenderableTraceResult<Object> trace(Vec3d start, Vec3d end) {
			return RayTraceUtil.miss(null);
		}

		@Override
		public TraceablePrimitive<Object> clone() {
			return this;
		}

		@Override
		public void rotate(int yRotation) {}

		@Override
		public void translate(Vec3d vec) {}

		@Override
		public void apply(Matrix4 matrix) {}

		@Override
		public Vec3d[] points() {
			return new Vec3d[0];
		}
		
		@Override
		public Vec3d[] edges() {
			return new Vec3d[0];
		}
	};
}
