FROM gitpod/workspace-full-vnc
RUN sudo apt update
RUN sudo apt install -y xorg-dev
