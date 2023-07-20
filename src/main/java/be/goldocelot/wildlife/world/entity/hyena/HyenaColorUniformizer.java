package be.goldocelot.wildlife.world.entity.hyena;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HyenaColorUniformizer {
    private static HyenaColorUniformizer instance;
    private List<HyenaData> latestHyenas = new ArrayList<>();
    private Random random = new Random();

    private HyenaColorUniformizer(){}
    public Hyena.HyenaGroupData uniformizeHyenaColor(Vec3 hyenaPosition) {
        hyenaPosition = getTwoDimVec3(hyenaPosition);
        Hyena.HyenaGroupData hyenaGroupData = getRandomHyenaData();

        if(!latestHyenas.isEmpty()){
            HyenaData nearHyena = getNearHyena(hyenaPosition);
            if(nearHyena != null){
                hyenaGroupData = new Hyena.HyenaGroupData(nearHyena.color, nearHyena.pattern);
            }
        }

        latestHyenas.add(new HyenaData(hyenaPosition, hyenaGroupData.color, hyenaGroupData.pattern));
        removeOldestHyena();

        return hyenaGroupData;
    }

    private void removeOldestHyena(){
        if(latestHyenas.size() > 8){
            latestHyenas.remove(0);
        }
    }

    private Vec3 getTwoDimVec3(Vec3 vec3){
        return new Vec3(vec3.x(),0,vec3.z());
    }

    private HyenaData getNearHyena(Vec3 hyenaPosition){
        HyenaData nearHyena = null;
        Double distance = Double.MAX_VALUE;

        for(HyenaData hyenaData : latestHyenas){
            double dist = hyenaData.position.distanceTo(hyenaPosition);
            if(dist < distance){
                nearHyena = hyenaData;
                distance = dist;
            }
        }
        return distance <= 5 ? nearHyena : null;
    }

    private Hyena.HyenaGroupData getRandomHyenaData(){
        return new Hyena.HyenaGroupData(HyenaColor.values()[random.nextInt(HyenaColor.values().length)], HyenaPattern.values()[random.nextInt(HyenaPattern.values().length)]);
    }

    public static HyenaColorUniformizer getInstance() {
        if(instance == null) instance = new HyenaColorUniformizer();
        return instance;
    }

    public class HyenaData{
        public Vec3 position;
        public HyenaColor color;
        public HyenaPattern pattern;

        private HyenaData(Vec3 position, HyenaColor color, HyenaPattern pattern){
            this.position = position;
            this.color = color;
            this.pattern = pattern;
        }
    }
}
