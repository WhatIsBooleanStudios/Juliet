FROM gitpod/gitpod/workspace-full-vnc
RUN sudo apt install -y xorg-devel
RUN sudo apt install -y mesa-dev