#!/bin/sh

mkdir rendsite
mv rendsite-binary-package.zip rendsite
cd rendsite
unzip rendsite-binary-package.zip
rm -rf rendsite-binary-package.zip
rm -rf /rendsite/doc
