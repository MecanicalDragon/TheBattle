const path = require('path');

// Add module resolution for IDEA coding assistance
module.exports = {
    resolve: {
        extensions: ['.js', '.jsx'],
        // Select JS sources
        modules: [
            path.join(__dirname, 'node_modules'),
            path.join(__dirname, 'src/the_battle')
        ],
        symlinks: false,
        alias: {
            '@': path.resolve(__dirname, 'src/the_battle')
        }
    }
};
