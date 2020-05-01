import React, {
    useState,
    useEffect,
    useRef,
    memo
} from 'react';
import PropTypes from 'prop-types';

const TheTint = ({
                     fallback = <span/>,
                     src,
                     color,
                     height,
                     width
                 }) => {
    const [size, setSize] = useState({})
    const canvasRef = useRef(null);

    const scaleImage = (image, outputWidth, outputHeight) => {
        let rate = 1;
        if (outputWidth && outputHeight) {
            rate = Math.min(outputWidth / image.width, outputHeight / image.height);
        } else if (outputWidth) {
            rate = outputWidth / image.width;
        } else if (outputHeight) {
            rate = outputHeight / image.height;
        }
        return {height: rate * image.height, width: rate * image.width}
    };

    const drawTint = () => {
        let canvas = canvasRef.current;
        let canvasContext = canvas.getContext('2d');
        let tint = document.createElement('canvas');
        let tintContext = tint.getContext('2d');
        let image = new Image();
        image.src = src;
        image.onload = () => {
            let size = scaleImage(image, width, height);
            setSize(size)
            tint.height = size.height;
            tint.width = size.width;
            tintContext.fillStyle = color;
            tintContext.fillRect(0, 0, size.width, size.height);
            tintContext.globalCompositeOperation = 'destination-atop';
            tintContext.drawImage(image, 0, 0, size.width, size.height);
            canvasContext.clearRect(0, 0, size.width, size.height);
            canvasContext.globalAlpha = 1;
            canvasContext.drawImage(tint, 0, 0, size.width, size.height);
        };
    };

    useEffect(drawTint, []);
    useEffect(drawTint, [color, src]);

    if (typeof window !== 'undefined' && window.document && window.document.createElement) {
        return (<canvas height={size.height} width={size.width} ref={canvasRef}/>);
    } else return fallback;
};

TheTint.propTypes = {
    fallback: PropTypes.node,
    src: PropTypes.string.isRequired,
    color: PropTypes.string.isRequired,
    height: PropTypes.string,
    width: PropTypes.string
};

export default memo(TheTint);