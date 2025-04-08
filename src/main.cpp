#define cimg_display 0
#include "CImg.h"
#include <random>

using namespace cimg_library;

const int N = 1000;   // nombre d'échantillons
const int H = 400;    // taille de l'image
const double D = 4.0; // domaine [-D/2, D/2]

void genererEchantillons() {
    CImg<unsigned char> image(H, H, 1, 3, 255); // image blanche RGB

    std::default_random_engine generator;
    std::normal_distribution<double> distribution(0.0, 1.0);

    unsigned char blue[] = {0, 0, 255};

    for (int i = 0; i < N; ++i) {
        double x = distribution(generator);
        double y = distribution(generator);

        int px = static_cast<int>((x + D / 2.0) * (H / D));
        int py = static_cast<int>((y + D / 2.0) * (H / D));

        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                int nx = px + dx;
                int ny = py + dy;
                if (nx >= 0 && nx < H && ny >= 0 && ny < H)
                    image.draw_point(nx, ny, blue);
            }
        }
    }

    image.save_bmp("distribution.bmp");  // Format BMP nativement supporté par CImg
}

int main() {
    genererEchantillons();
    return 0;
}
