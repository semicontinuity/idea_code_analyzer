package com.metalogic;

/**
 * This class would be useful for graph layout algorithms
 * @author Igor A. Karpov (ikar)
 */
public class GeometryUtil
{
    /** Determine if two line segments (not lines) intersect (not touch) */
    public static boolean intersects (
        final long l1x1,
        final long l1y1,
        final long l1x2,
        final long l1y2,
        final long l2x1,
        final long l2y1,
        final long l2x2,
        final long l2y2)
    {
        return !onTheSameSide (l1x1, l1y1, l1x2, l1y2, l2x1, l2y1, l2x2, l2y2)
            && !onTheSameSide (l2x1, l2y1, l2x2, l2y2, l1x1, l1y1, l1x2, l1y2);
    }

    /**
     * Determine if the line segment with coordinates (x1,y1)-(x2,y2)
     * lies strictly on the same side of line drawn thru (refX1,refY1) and (refX2,refY2).
     * <p/>
     * The line drawn thru (refX1,refY1) and (refX2,refY2) has an equation
     * <pre bgcolor="#E0E0E0">(refY2-refY1)(x-refX1) - (refX2-refX1)(y-refY1) = 0</pre>
     * <p/>
     * unwinding, we get
     * <pre bgcolor="#E0E0E0">(refY2-refY1)x - refX1*refY2 + refY1*refX1 + (refX1-refX2)y + (refX2-refX1)refY1 = 0</pre>
     * The generic form for the equation representing a line is <code>ax + by + c = 0</code>,
     * so
     * <pre bgcolor="#E0E0E0">
     * a = refY2-refY1
     * b = refX1-refX2
     * c = refX2*refY1 - refX1*refY2</pre>
     * If some point (x0,y0) is placed to <code>ax + by + c</code>, the absolute value of
     * the result will produce the distance to the line, and the sign will indicate,
     * on which side of the line the point is.
     * Thus, both points are on the same side of the line if they have the same signs.
     */
    protected static boolean onTheSameSide (
        final long refX1,
        final long refY1,
        final long refX2,
        final long refY2,
        final long x1,
        final long y1,
        final long x2,
        final long y2)
    {
        final long a = refY2 - refY1;
        final long b = refX1 - refX2;
        final long c = refX2 * refY1 - refX1 * refY2;

        final long value1 = a * x1 + b * y1 + c;
        final long value2 = a * x2 + b * y2 + c;

        return (value1 > 0 && value2 > 0) || (value1 < 0 && value2 < 0);
    }
}
