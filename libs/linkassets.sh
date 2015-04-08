#!/bin/sh
rm -r assets
mkdir -p assets
cd assets
ln -s ../../../assets/* .
ln -s ../extra_assets/* .
