#!/bin/sh
# Linux Deploy Component
# (c) Anton Skshidlevsky <meefik@gmail.com>, GPLv3

# 31 doesn't support while rpm2cpio in busybox doesn't support zstd
[ -n "${SUITE}" ] || SUITE="37"

if [ -z "${ARCH}" ]
then
    case "$(get_platform)" in
    x86) ARCH="i386" ;;
    x86_64) ARCH="x86_64" ;;
    arm) ARCH="armhfp" ;;
    arm_64) ARCH="aarch64" ;;
    esac
fi

[ -n "${SOURCE_PATH}" ] || SOURCE_PATH="http://dl.fedoraproject.org/pub/"

dnf_install()
{
    local packages="$@"
    [ -n "${packages}" ] || return 1
    (set -e
        chroot_exec -u root dnf -y install ${packages}
    exit 0)
    return $?
}

do_install()
{
    is_archive "${SOURCE_PATH}" && return 0

    msg ":: Installing ${COMPONENT} ... "

    local core_packages="alternatives audit-libs authselect authselect-libs basesystem bash bzip2-libs ca-certificates coreutils coreutils-common cracklib crypto-policies cryptsetup-libs curl cyrus-sasl-lib dbus dbus-broker dbus-common device-mapper device-mapper-libs diffutils dnf dnf-data elfutils-default-yama-scope elfutils-libelf elfutils-libs expat file-libs filesystem findutils gawk gdbm-libs glib2 glibc glibc-common glibc-minimal-langpack gmp gnupg2 gnutls gpgme grep gzip ima-evm-utils json-c keyutils-libs kmod-libs krb5-libs libacl libarchive libargon2 libassuan libattr libb2 libblkid libbpf libbrotli libcap libcap-ng libcom_err libcomps libcurl libdb libdnf libeconf libevent libfdisk libffi libfsverity libgcc libgcrypt libgomp libgpg-error libidn2 libksba libmodulemd libmount libnghttp2 libnsl2 libpsl libpwquality librepo libreport-filesystem libseccomp libselinux libsemanage libsepol libsigsegv libsmartcols libsolv libssh libssh-config libstdc++ libtasn1 libtirpc libunistring libuser libutempter libuuid libverto libxcrypt libxkbcommon libxml2 libyaml libzstd lua-libs lz4-libs mpdecimal mpfr ncurses-base ncurses-libs nettle npth openldap openssl openssl-libs p11-kit p11-kit-trust pam pam-libs pcre pcre2 pcre2-syntax popt publicsuffix-list-dafsa python-pip-wheel python-setuptools-wheel python3 python3-dnf python3-gpg python3-hawkey python3-libcomps python3-libdnf python3-libs python3-rpm qrencode-libs readline rootfiles rpm rpm-build-libs rpm-libs rpm-sign-libs sed setup shadow-utils sqlite-libs sudo systemd systemd-libs systemd-networkd systemd-pam tar tpm2-tss tzdata util-linux util-linux-core util-linux-user vim-data vim-minimal xkeyboard-config xz-libs yum zchunk-libs zlib fedora-release-identity-container fedora-gpg-keys fedora-repos fedora-release fedora-release-common fedora-repos-modular glibc-locale-source"


    local repo_url
    if [ "${ARCH}" = "i386" ]
    then 
    	repo_url="${SOURCE_PATH%/}/fedora-secondary/releases/${SUITE}/Everything/${ARCH}/os"
	else
		repo_url="${SOURCE_PATH%/}/fedora/linux/releases/${SUITE}/Everything/${ARCH}/os"
		wget -q ${SOURCE_PATH%/}/fedora/linux/releases/${SUITE}/Everything/${ARCH}/os || repo_url="${SOURCE_PATH%/}/releases/${SUITE}/Everything/${ARCH}/os"
    fi

    msg -n "Preparing for deployment ... "
  ${tar_prefix}  tar xzf "${COMPONENT_DIR}/filesystem.tgz" -C "${CHROOT_DIR}"
    is_ok "fail" "done" || return 1

    msg -n "Retrieving packages list ... "
    local pkg_list="${CHROOT_DIR}/tmp/packages.list"
    (set -e
        repodata=$(wget -q -O - "${repo_url}/repodata/repomd.xml" | sed -n '/<location / s/^.*<location [^>]*href="\([^\"]*\-primary\.xml\.gz\)".*$/\1/p')
        [ -z "${repodata}" ] && exit 1
        wget -q -O - "${repo_url}/${repodata}" | gzip -dc | sed -n '/<location / s/^.*<location [^>]*href="\([^\"]*\)".*$/\1/p' > "${pkg_list}"
    exit 0)
    is_ok "fail" "done" || return 1

    msg "Retrieving packages: "
    local package i pkg_url pkg_file pkg_arch
    case "${ARCH}" in
    i386) pkg_arch="-e i686 -e noarch" ;;
    x86_64) pkg_arch="-e x86_64 -e noarch" ;;
    armhfp) pkg_arch="-e armv7hl -e noarch" ;;
    aarch64) pkg_arch="-e aarch64 -e noarch" ;;
    esac
    for package in ${core_packages}
    do
        msg -n "${package} ... "
        pkg_url=$(grep -e "^.*/${package}-[0-9r][0-9\.\-].*rpm$" "${pkg_list}" | grep -m1 ${pkg_arch})
        test "${pkg_url}"; is_ok "fail" || return 1
        pkg_file="${pkg_url##*/}"
        # download
        for i in 1 2 3
        do
            wget -q -c -O "${CHROOT_DIR}/tmp/${pkg_file}" "${repo_url}/${pkg_url}" && break
            sleep 30s
        done
        [ "${package}" = "filesystem" ] && { msg "done"; continue; }
        # unpack
        (cd "${CHROOT_DIR}"; rpm2cpio "./tmp/${pkg_file}" | ${tar_prefix} cpio -idmu --quiet >/dev/null)
        is_ok "fail" "done" || return 1
    done

    component_exec core/emulator
      return 1
    msg "Installing packages ... "
    chroot_exec /bin/rpm -i --force --nosignature --nodeps /tmp/*.rpm
    
    #is_ok || return 1

    msg -n "Clearing cache ... "
    rm -rf "${CHROOT_DIR}"/tmp/*
    is_ok "skip" "done"

    component_exec core/mnt core/net

    msg -n "Setting dnf excludes ..."
    echo "exclude=grubby" >> "${CHROOT_DIR}"/etc/dnf/dnf.conf
    is_ok "fail" "done"

    msg -n "Upgrading packages ..."
    chroot_exec -u root dnf -y upgrade --refresh
    is_ok "fail" "done"

    if [ -n "${EXTRA_PACKAGES}" ]; then
      msg "Installing extra packages: "
      dnf_install ${EXTRA_PACKAGES}
      is_ok || return 1
    fi

    return 0
}

do_help()
{
cat <<EOF
   --arch="${ARCH}"
     Architecture of Linux distribution, supported "armhfp", "aarch64", "i386" and "x86_64".

   --suite="${SUITE}"
     Version of Linux distribution, supported version "28".

   --source-path="${SOURCE_PATH}"
     Installation source, can specify address of the repository or path to the rootfs archive.

   --extra-packages="${EXTRA_PACKAGES}"
     List of optional installation packages, separated by spaces.

EOF
}
