package com.chimericdream.camelnostrils.client.model;

import net.minecraft.client.model.animal.camel.AdultCamelModel;
import net.minecraft.client.model.animal.camel.BabyCamelModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Vanilla bakes a camel's whole head (jaw + skull + snout) as three cubes inside one ModelPart, and
 * ModelPart only supports hiding an entire part, not individual cubes within it. So "removing the
 * snout" means baking a second, otherwise-identical body mesh with that one cube left out, then
 * swapping the renderer's active model to it - the same technique vanilla itself uses to swap between
 * adult and baby camel models.
 */
public final class CN$CamelModels {
    private static AdultCamelModel noSnoutAdultModel;
    private static BabyCamelModel noSnoutBabyModel;

    private CN$CamelModels() {
    }

    public static AdultCamelModel noSnoutAdultModel() {
        if (noSnoutAdultModel == null) {
            noSnoutAdultModel = new AdultCamelModel(createNoSnoutAdultBodyLayer().bakeRoot());
        }

        return noSnoutAdultModel;
    }

    public static BabyCamelModel noSnoutBabyModel() {
        if (noSnoutBabyModel == null) {
            noSnoutBabyModel = new BabyCamelModel(createNoSnoutBabyBodyLayer().bakeRoot());
        }

        return noSnoutBabyModel;
    }

    // Copied from AdultCamelModel.createBodyMesh(), minus the head's third addBox() call (the
    // forward-most, narrower cube - the snout).
    private static LayerDefinition createNoSnoutAdultBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild(
            "body", CubeListBuilder.create().texOffs(0, 25).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F), PartPose.offset(0.0F, 4.0F, 9.5F)
        );
        body.addOrReplaceChild(
            "hump", CubeListBuilder.create().texOffs(74, 0).addBox(-4.5F, -5.0F, -5.5F, 9.0F, 5.0F, 11.0F), PartPose.offset(0.0F, -12.0F, -10.0F)
        );
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(122, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F), PartPose.offset(0.0F, -9.0F, 3.5F));
        PartDefinition head = body.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(60, 24)
                .addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F)
                .texOffs(21, 0)
                .addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F),
            PartPose.offset(0.0F, -3.0F, -19.5F)
        );
        head.addOrReplaceChild(
            "left_ear", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(2.5F, -21.0F, -9.5F)
        );
        head.addOrReplaceChild(
            "right_ear", CubeListBuilder.create().texOffs(67, 0).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(-2.5F, -21.0F, -9.5F)
        );
        root.addOrReplaceChild(
            "left_hind_leg", CubeListBuilder.create().texOffs(58, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, 9.5F)
        );
        root.addOrReplaceChild(
            "right_hind_leg", CubeListBuilder.create().texOffs(94, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, 9.5F)
        );
        root.addOrReplaceChild(
            "left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, -10.5F)
        );
        root.addOrReplaceChild(
            "right_front_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, -10.5F)
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    // Copied from BabyCamelModel.createBodyLayer(), minus the head's third addBox() call (the
    // forward-most, narrower cube - the snout).
    private static LayerDefinition createNoSnoutBabyBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild(
            "body", CubeListBuilder.create().texOffs(0, 14).addBox(-4.5F, -4.0F, -8.0F, 9.0F, 8.0F, 16.0F), PartPose.offset(0.0F, 7.0F, 0.0F)
        );
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(50, 38).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 9.0F, 0.0F), PartPose.offset(0.0F, -1.5F, 8.05F));
        PartDefinition head = body.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(20, 0)
                .addBox(-2.5F, -3.0F, -7.5F, 5.0F, 5.0F, 7.0F)
                .texOffs(0, 0)
                .addBox(-2.5F, -12.0F, -7.5F, 5.0F, 9.0F, 5.0F),
            PartPose.offset(0.0F, 1.0F, -7.5F)
        );
        head.addOrReplaceChild(
            "right_ear", CubeListBuilder.create().texOffs(37, 0).addBox(-3.0F, -0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(-2.5F, -11.0F, -4.0F)
        );
        head.addOrReplaceChild(
            "left_ear", CubeListBuilder.create().texOffs(47, 0).addBox(0.0F, -0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(2.5F, -11.0F, -4.0F)
        );
        root.addOrReplaceChild(
            "right_front_leg", CubeListBuilder.create().texOffs(36, 14).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(-3.0F, 11.5F, -5.5F)
        );
        root.addOrReplaceChild(
            "left_front_leg", CubeListBuilder.create().texOffs(48, 14).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(3.0F, 11.5F, -5.5F)
        );
        root.addOrReplaceChild(
            "left_hind_leg", CubeListBuilder.create().texOffs(12, 38).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(3.0F, 11.5F, 5.5F)
        );
        root.addOrReplaceChild(
            "right_hind_leg", CubeListBuilder.create().texOffs(0, 38).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(-3.0F, 11.5F, 5.5F)
        );
        return LayerDefinition.create(mesh, 64, 64);
    }
}
