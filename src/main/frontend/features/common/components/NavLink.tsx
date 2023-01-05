import Link from "next/link";
import { useRouter } from "next/router";

interface Props {
  href: string;
  label: string;
}

export const NavLink: React.FC<Props> = ({ href, label }) => {
  const router = useRouter();
  return (

    <Link href={href}>
      <a
        className={
          { color: router.pathname === href ? "active" : "" } + " nav-link"
        }
      >
        {label}
      </a>
    </Link>
  );
};
