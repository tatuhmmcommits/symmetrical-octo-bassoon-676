package net.charter.client;

import net.charter.CharterMod;
import net.charter.entity.DivineDominanceEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/**
 * Draws Divine Dominance: the pillar (1.5 blocks wide, 3 blocks tall) with the sigil of Charter on each of its
 * four sides, the chains wrapped and orbiting around it, the sigil on the ground beneath it, and the golden walls
 * that close in around it. Everything is drawn from the tracked values of the entity and the world time.
 *
 * <p>Textures under {@code assets/charter/textures/entity/} (see {@code docs/TEXTURES.md}):
 * <ul>
 * <li>{@code dominance_pillar.png}: 64 x 64. The left half (32 x 64) is the front of the pillar, drawn top to
 * bottom as capital (8 px), shaft (48 px, only the middle 24 px of the width) and plinth (8 px); the right half
 * holds the stone of the ledges (32 x 16, at 32,16) and of the top (32 x 32, at 32,32).</li>
 * <li>{@code charter_sigil.png}: the sigil, on the shaft and on the ground.</li>
 * <li>{@code charter_chain.png}: the chains (32 x 3 strip).</li>
 * <li>{@code cage.png}: the walls.</li>
 * </ul>
 */
public class DivineDominanceRenderer extends EntityRenderer<DivineDominanceEntity> {
	private static final Identifier PILLAR = CharterMod.id("textures/entity/dominance_pillar.png");
	private static final Identifier CAGE = CharterMod.id("textures/entity/cage.png");

	/** Blocks covered by one repeat of the wall texture. */
	private static final float CAGE_TILE = 4.0f;

	private static final float HEIGHT = DivineDominanceEntity.HEIGHT;
	/** Half the width of the plinth and the capital: the pillar is 1.5 blocks wide at its widest. */
	private static final float WIDE = DivineDominanceEntity.WIDTH * 0.5f;
	/** Half the width of the shaft. */
	private static final float SHAFT = 0.5625f;
	private static final float PLINTH_TOP = 0.375f;
	private static final float CAP_BOTTOM = HEIGHT - 0.375f;
	/** Where the middle of the sigil sits on the shaft, and how big it is. */
	private static final float SIGIL_MIDDLE = HEIGHT * 0.5f;
	private static final float SIGIL_HALF = 0.46f;

	public DivineDominanceRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.0f;
	}

	@Override
	public Identifier getTexture(DivineDominanceEntity entity) {
		return PILLAR;
	}

	/** The walls are far bigger than the pillar, so the default culling would hide them. */
	@Override
	public boolean shouldRender(DivineDominanceEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}

	@Override
	public void render(DivineDominanceEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider providers, int light) {
		boolean armed = entity.getStart() >= 0L;
		float age = armed ? entity.age(tickDelta) : 0.0f;

		if (armed) {
			renderGroundSigil(age, matrices, providers);
		}
		renderPillar(entity, age, armed, matrices, providers, light);
		if (armed) {
			renderWalls(entity, age, matrices, providers);
		}
	}

	// ------------------------------------------------------------------ the sigil on the ground

	private static void renderGroundSigil(float age, MatrixStack matrices, VertexConsumerProvider providers) {
		float alpha = CharterFxRenderer.clamp01(age / 24.0f) * (0.80f + 0.12f * MathHelper.sin(age * 0.12f));
		if (alpha <= 0.001f) {
			return;
		}
		VertexConsumer consumer = providers.getBuffer(RenderLayer.getEntityTranslucentEmissive(CharterFxRenderer.SIGIL));
		matrices.push();
		matrices.translate(0.0, 0.04, 0.0);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 0.5f));
		CharterFxRenderer.flat(consumer, matrices.peek(), 2.4f, 1.0f, 1.0f, 1.0f, alpha);
		matrices.pop();
	}

	// ------------------------------------------------------------------ the pillar

	private void renderPillar(DivineDominanceEntity entity, float age, boolean armed, MatrixStack matrices, VertexConsumerProvider providers, int light) {
		// the pillar climbs out of the ground when it is raised
		float rise = armed ? CharterFxRenderer.easeOut(CharterFxRenderer.clamp01(age / 40.0f)) : 1.0f;
		matrices.push();
		matrices.translate(0.0, (double) ((rise - 1.0f) * HEIGHT), 0.0);

		// the stone: three boxes (plinth, shaft, capital), each side cut from the front elevation in the texture
		VertexConsumer body = providers.getBuffer(RenderLayer.getEntityCutoutNoCull(PILLAR));
		for (int side = 0; side < 4; side++) {
			matrices.push();
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f * (float) side));
			MatrixStack.Entry entry = matrices.peek();
			front(body, entry, WIDE, CAP_BOTTOM, HEIGHT, 0.0f, 0.0f, 0.5f, 0.125f, light);
			front(body, entry, SHAFT, PLINTH_TOP, CAP_BOTTOM, 0.0625f, 0.125f, 0.4375f, 0.875f, light);
			front(body, entry, WIDE, 0.0f, PLINTH_TOP, 0.0f, 0.875f, 0.5f, 1.0f, light);
			matrices.pop();
		}
		MatrixStack.Entry flat = matrices.peek();
		horizontal(body, flat, WIDE, HEIGHT, true, 0.5f, 0.5f, 1.0f, 1.0f, light);
		horizontal(body, flat, WIDE, CAP_BOTTOM, false, 0.5f, 0.25f, 1.0f, 0.5f, light);
		horizontal(body, flat, WIDE, PLINTH_TOP, true, 0.5f, 0.25f, 1.0f, 0.5f, light);

		float glow = armed ? CharterFxRenderer.clamp01(age / 30.0f) * (0.88f + 0.12f * MathHelper.sin(age * 0.15f)) : 0.7f;
		float chainAlpha = armed ? CharterFxRenderer.clamp01(age / 20.0f) : 1.0f;

		// the sigil of Charter on every side of the shaft, glowing
		if (glow > 0.001f) {
			VertexConsumer sigil = providers.getBuffer(RenderLayer.getEntityTranslucentEmissive(CharterFxRenderer.SIGIL));
			for (int side = 0; side < 4; side++) {
				matrices.push();
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f * (float) side));
				MatrixStack.Entry entry = matrices.peek();
				float z = SHAFT + 0.004f;
				CharterFxRenderer.vertex(sigil, entry, -SIGIL_HALF, SIGIL_MIDDLE - SIGIL_HALF, z, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, glow);
				CharterFxRenderer.vertex(sigil, entry, SIGIL_HALF, SIGIL_MIDDLE - SIGIL_HALF, z, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, glow);
				CharterFxRenderer.vertex(sigil, entry, SIGIL_HALF, SIGIL_MIDDLE + SIGIL_HALF, z, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, glow);
				CharterFxRenderer.vertex(sigil, entry, -SIGIL_HALF, SIGIL_MIDDLE + SIGIL_HALF, z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, glow);
				matrices.pop();
			}
		}

		// the chains: tight wraps above and below the sigil, and two loose chains orbiting the whole pillar
		if (chainAlpha > 0.001f) {
			VertexConsumer chains = providers.getBuffer(RenderLayer.getEntityTranslucentEmissive(CharterFxRenderer.CHAIN));
			MatrixStack.Entry entry = matrices.peek();
			float thick = CharterFxRenderer.CHAIN_TILE * CharterFxRenderer.CHAIN_ASPECT * 1.5f;
			float crawl = age * 0.006f;
			wrap(chains, entry, 0.62f, SHAFT + 0.03f, thick, crawl, chainAlpha);
			wrap(chains, entry, 0.88f, SHAFT + 0.03f, thick, -crawl, chainAlpha);
			wrap(chains, entry, HEIGHT - 0.88f, SHAFT + 0.03f, thick, crawl, chainAlpha);
			wrap(chains, entry, HEIGHT - 0.62f, SHAFT + 0.03f, thick, -crawl, chainAlpha);
			CharterFxRenderer.helix(chains, entry, 0.12f, HEIGHT - 0.12f, 1.15f, 2.5f, thick, 40, age * 1.2f, chainAlpha);
			CharterFxRenderer.helix(chains, entry, 0.12f, HEIGHT - 0.12f, 1.2f, -2.5f, thick, 40, 70.0f - age * 1.0f, chainAlpha);
		}
		matrices.pop();
	}

	/** One face of a box that faces +Z; the pillar is turned a quarter turn at a time to draw the other three. */
	private static void front(VertexConsumer consumer, MatrixStack.Entry entry, float half, float y0, float y1, float u0, float v0, float u1, float v1, int light) {
		lit(consumer, entry, -half, y0, half, u0, v1, 0.0f, 0.0f, 1.0f, light);
		lit(consumer, entry, half, y0, half, u1, v1, 0.0f, 0.0f, 1.0f, light);
		lit(consumer, entry, half, y1, half, u1, v0, 0.0f, 0.0f, 1.0f, light);
		lit(consumer, entry, -half, y1, half, u0, v0, 0.0f, 0.0f, 1.0f, light);
	}

	/** A square face of a box, lying flat. */
	private static void horizontal(VertexConsumer consumer, MatrixStack.Entry entry, float half, float y, boolean facingUp, float u0, float v0, float u1, float v1, int light) {
		float ny = facingUp ? 1.0f : -1.0f;
		lit(consumer, entry, -half, y, -half, u0, v0, 0.0f, ny, 0.0f, light);
		lit(consumer, entry, -half, y, half, u0, v1, 0.0f, ny, 0.0f, light);
		lit(consumer, entry, half, y, half, u1, v1, 0.0f, ny, 0.0f, light);
		lit(consumer, entry, half, y, -half, u1, v0, 0.0f, ny, 0.0f, light);
	}

	private static void lit(VertexConsumer consumer, MatrixStack.Entry entry, float x, float y, float z, float u, float v, float nx, float ny, float nz, int light) {
		consumer.vertex(entry.getPositionMatrix(), x, y, z)
				.color(1.0f, 1.0f, 1.0f, 1.0f)
				.texture(u, v)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(entry.getNormalMatrix(), nx, ny, nz)
				.next();
	}

	/** A loop of chain pulled tight around the shaft at height {@code y}: four straight runs along its sides. */
	private static void wrap(VertexConsumer consumer, MatrixStack.Entry entry, float y, float half, float height, float shift, float alpha) {
		float tiles = 2.0f * half / CharterFxRenderer.CHAIN_TILE;
		float bottom = y - height * 0.5f;
		float top = y + height * 0.5f;
		float u = shift;
		CharterFxRenderer.band(consumer, entry, -half, -half, bottom, top, half, -half, bottom, top, u, u + tiles, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, alpha);
		u += tiles;
		CharterFxRenderer.band(consumer, entry, half, -half, bottom, top, half, half, bottom, top, u, u + tiles, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, alpha);
		u += tiles;
		CharterFxRenderer.band(consumer, entry, half, half, bottom, top, -half, half, bottom, top, u, u + tiles, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, alpha);
		u += tiles;
		CharterFxRenderer.band(consumer, entry, -half, half, bottom, top, -half, -half, bottom, top, u, u + tiles, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, alpha);
	}

	// ------------------------------------------------------------------ the walls

	private static void renderWalls(DivineDominanceEntity entity, float age, MatrixStack matrices, VertexConsumerProvider providers) {
		int arm = entity.getArm();
		float half = DivineDominanceEntity.halfWidth(entity.getStartHalf(), entity.getEndHalf(), arm, entity.getShrink(), age);
		float up = (float) entity.getUp();
		float down = (float) entity.getDown();

		// the walls rise out of the ground when the pillar is raised
		float rise = CharterFxRenderer.easeOut(CharterFxRenderer.clamp01(age / 40.0f));
		float top = -down + (up + down) * rise;
		boolean live = age >= (float) arm;
		float flash = live ? CharterFxRenderer.clamp01(1.0f - (age - (float) arm) / 12.0f) : 0.0f;
		float closing = live ? CharterFxRenderer.clamp01((age - (float) arm) / (float) Math.max(1, entity.getShrink())) : 0.0f;
		float alpha = (live ? 0.62f : 0.30f) + 0.08f * MathHelper.sin(age * 0.2f) + 0.30f * flash;
		alpha = CharterFxRenderer.clamp01(alpha * CharterFxRenderer.clamp01(age / 10.0f));
		if (alpha <= 0.001f) {
			return;
		}
		float green = 0.82f - 0.22f * closing;
		float blue = 0.32f - 0.12f * closing;
		float scroll = age * 0.012f;
		float vSpan = (top + down) / CAGE_TILE;
		float uSpan = 2.0f * half / CAGE_TILE;

		VertexConsumer consumer = providers.getBuffer(RenderLayer.getEntityTranslucentEmissive(CAGE));
		MatrixStack.Entry entry = matrices.peek();
		// north, east, south and west wall
		CharterFxRenderer.band(consumer, entry, -half, -half, -down, top, half, -half, -down, top, 0.0f, uSpan, scroll, scroll + vSpan, 1.0f, green, blue, alpha);
		CharterFxRenderer.band(consumer, entry, half, -half, -down, top, half, half, -down, top, 0.0f, uSpan, scroll, scroll + vSpan, 1.0f, green, blue, alpha);
		CharterFxRenderer.band(consumer, entry, half, half, -down, top, -half, half, -down, top, 0.0f, uSpan, scroll, scroll + vSpan, 1.0f, green, blue, alpha);
		CharterFxRenderer.band(consumer, entry, -half, half, -down, top, -half, -half, -down, top, 0.0f, uSpan, scroll, scroll + vSpan, 1.0f, green, blue, alpha);
	}
}
