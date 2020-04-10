import image from '@/img/sage.png';

function markTargets(position, attacker, target) {
    return ["pos1", "pos2", "pos3", "pos4", "pos5"];
}

export default function properties(){
    return {
        image: image,
        markTargets: markTargets
    }
}