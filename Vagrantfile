# -*- mode: ruby -*-
# vi: set ft=ruby :
Vagrant.configure(2) do |config|

  config.vm.box = "ubuntu/bionic64"
  config.vm.network "private_network", ip: "10.11.12.100"
  config.vm.provider "virtualbox" do |vb|
    vb.memory = "1024"
  end

  config.vm.provision "shell", privileged: false, inline: <<-SHELL
    sudo apt-get update
    sudo apt-get install openjdk-8-jdk -y
    sudo update-java-alternatives --set java-1.8.0-openjdk-amd64
    sudo apt-get install maven git mercurial subversion -y

    echo 'export MAVEN_OPTS="-Xms256m -Xmx512m"' >> ~/.profile

    # Configuring dev.helixteamhub.com hostname
    if ! grep "^10.11.12.13 dev.helixteamhub.com$" /etc/hosts &> /dev/null ; then
      echo '10.11.12.13 dev.helixteamhub.com' | sudo tee -a /etc/hosts > /dev/null
    fi

    # Configuring gconn.helixteamhub.com hostname
    if ! grep "^10.11.12.1 gconn.helixteamhub.com$" /etc/hosts &> /dev/null ; then
      echo '10.11.12.1 gconn.helixteamhub.com' | sudo tee -a /etc/hosts > /dev/null
    fi
  SHELL
end
